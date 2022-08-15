package com.muy.service;

import com.google.common.collect.Maps;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.DefinitionsScopedSearch;
import com.muy.common.tree.enums.MethodType;
import com.muy.domain.bean.invoketree.TreeInvokeModel;
import com.muy.domain.bean.invoketree.TreeNodeModel;
import com.muy.service.filters.ImplementClassFilter;
import com.muy.utils.SequenceOutlinePsiUtils;
import com.muy.utils.MapStack;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生成时序图
 *
 * @Author jiyanghuang
 * @Date 2022/6/24 00:35
 */
public class SequenceGenerator extends JavaRecursiveElementVisitor {

    private static final Logger LOGGER = Logger.getInstance(SequenceGenerator.class);

//    private final Stack<Integer> offsetStack = new Stack<>();

    private MapStack<TreeInvokeModel> topStack;

    private final SequenceParams params;

    @Getter
    @Setter
    private SequenceParams paramsStack;

    @Getter
    private TreeInvokeModel root;

    private int depth;

    private final ImplementationFinder implementationFinder = new ImplementationFinder();

    /**
     * 表示当前行最大的列
     */
    private Map<Integer, AtomicInteger> rowColIndexMap = Maps.newConcurrentMap();

    public SequenceGenerator(SequenceParams params) {
        this.params = params;
        topStack = new MapStack<TreeInvokeModel>(TreeInvokeModel::getUriMd5);
    }

    public TreeInvokeModel generate(PsiElement psiElement) {
        if (psiElement instanceof PsiMethod)
            return generate((PsiMethod) psiElement);
        else if (psiElement instanceof PsiLambdaExpression) {
            return generate((PsiLambdaExpression) psiElement);
        } else {
            LOGGER.warn("unsupported " + psiElement.getText());
        }

        return null;
    }

    public TreeInvokeModel generate(PsiMethod psiMethod) {
        if (psiMethod.getLanguage().equals(JavaLanguage.INSTANCE)) {
            return generateJava(psiMethod);
        } else {
//            return topStack;
            return null;
        }
    }

    /**
     * Generate Java method
     *
     * @param psiMethod Java method
     * @return CallStack
     */
    private TreeInvokeModel generateJava(PsiMethod psiMethod) {
        PsiClass containingClass = psiMethod.getContainingClass();
        if (containingClass == null) {
            containingClass = (PsiClass) psiMethod.getParent().getContext();
        }

        if (containingClass == null) {
//            return topStack;
            return null;
        }

        // follow implementation
        if (SequenceOutlinePsiUtils.isAbstract(containingClass)) {
            boolean currentAccept = false;
            PsiElement[] psiElements = DefinitionsScopedSearch.search(psiMethod).toArray(PsiElement.EMPTY_ARRAY);
            if (psiElements.length == 1) {
                currentAccept = true;
                methodAccept(psiElements[0]);
            } else {
                for (PsiElement psiElement : psiElements) {
                    if (psiElement instanceof PsiMethod) {
                        if (alreadyInStack((PsiMethod) psiElement)) {
                            continue;
                        }

                        if (params.getInterfaceImplFilter().allow((PsiMethod) psiElement)) {
                            currentAccept = true;
                            methodAccept(psiElement);
                        }
                    }
                }
            }
            // 如果当前没有找到实现类进行遍历，则遍历接口实现，否则用真实实现
            if (!currentAccept) {
                psiMethod.accept(this);
            }
        } else {
            // resolve variable initializer
            if (params.isSmartInterface() && !SequenceOutlinePsiUtils.isExternal(containingClass)) {
                containingClass.accept(implementationFinder);
            }

            psiMethod.accept(this);
        }
//        return topStack;
        return null;
    }

    private boolean alreadyInStack(PsiMethod psiMethod) {
        // Don't check external method, because the getTextOffset() will cause Java decompiler, it will wast of time.
        if (psiMethod.getContainingClass() == null || SequenceOutlinePsiUtils.isExternal(psiMethod.getContainingClass())) {
            return true;
        }
        final int offset = psiMethod.getTextOffset();
//        MethodDescription method = createMethod(psiMethod, offset);
//        return currentStack.isRecursive(method);
        return false;
    }

    private void methodAccept(PsiElement psiElement) {
        if (psiElement instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) psiElement;
            if (params.getMethodFilter().allow(method)) {
                PsiClass containingClass = (method).getContainingClass();
                if (params.isSmartInterface() && containingClass != null && !SequenceOutlinePsiUtils.isExternal(containingClass))
                    containingClass.accept(implementationFinder);
                method.accept(this);
            }
        }
    }

    private boolean methodAcceptStack(PsiElement psiElement) {
        if (psiElement instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) psiElement;
            if (paramsStack.getMethodFilter().allow(method)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void visitMethod(PsiMethod psiMethod) {
//        int offset = offsetStack.isEmpty() ? psiMethod.getTextOffset() : offsetStack.pop();
        TreeNodeModel treeNodeModel = TreeNodeModel.of(psiMethod);
        TreeInvokeModel treeInvokeModel = TreeInvokeModel.of(treeNodeModel);
        // 判断是否有循环
        boolean recursive = topStack.containsItem(treeInvokeModel);
        if(recursive){
            treeInvokeModel.getTreeNodeModel().setMethodType(MethodType.RECURSIVE_METHOD.getType());
        }
        if (methodAcceptStack(psiMethod)) {

            Integer row;
            if (topStack.size() <= 0) {
                row = 0;
            } else {
                row = topStack.peek().getRow() + 1;
            }
            treeInvokeModel.setRow(row);
            fillRowCol(treeInvokeModel, row);
            topStack.push(treeInvokeModel);
        }
        // 不是循环时再往下遍历
        if(!recursive){
            super.visitMethod(psiMethod);
        }
        if (methodAcceptStack(psiMethod)) {
            TreeInvokeModel stackBack = topStack.pop();
            if (topStack.size() > 0) {
                topStack.peek().getSubInvoke().add(stackBack);
            } else {
                root = stackBack;
            }
        }
    }

    @Override
    public void visitCallExpression(PsiCallExpression callExpression) {
        super.visitCallExpression(callExpression);
        PsiMethod psiMethod = callExpression.resolveMethod();
        findAbstractImplFilter(callExpression, psiMethod);
        methodCall(psiMethod, SequenceOutlinePsiUtils.findNaviOffset(callExpression));
    }

    @Override
    public void visitMethodReferenceExpression(PsiMethodReferenceExpression expression) {
        final PsiElement resolve = expression.resolve();
        if (resolve instanceof PsiMethod) {
            final PsiMethod psiMethod = (PsiMethod) resolve;
            final int offset = expression.getTextOffset();
            methodCall(psiMethod, offset);
        }
        super.visitMethodReferenceExpression(expression);
    }

    /**
     * If the psiMethod's containing class is Interface or abstract, then try to find its implement class.
     *
     * @param callExpression expression
     * @param psiMethod      method
     */
    private void findAbstractImplFilter(PsiCallExpression callExpression, PsiMethod psiMethod) {
        try {
            if (null == psiMethod) {
                return;
            }
            PsiClass containingClass = psiMethod.getContainingClass();
            if (SequenceOutlinePsiUtils.isAbstract(containingClass)) {
                String type = containingClass.getQualifiedName();
                if (type == null) {
                    return;
                }

                PsiMethodCallExpression psiMethodCallExpression = (PsiMethodCallExpression) callExpression;
                PsiExpression qualifierExpression = psiMethodCallExpression.getMethodExpression().getQualifierExpression();

                if (qualifierExpression == null) {
                    return;
                }

                PsiType psiType = qualifierExpression.getType();

                if (psiType == null) {
                    return;
                }

                String impl = psiType.getCanonicalText();

                if (!impl.startsWith(type)) {
                    params.getInterfaceImplFilter().put(type, new ImplementClassFilter(impl));
                }
            }
        } catch (Exception e) {
            //ignore
        }
    }

    private void methodCall(PsiMethod psiMethod, int offset) {
        if (psiMethod == null) {
            return;
        }
        if (!params.getMethodFilter().allow(psiMethod)) {
            return;
        }

        if (depth < params.getMaxDepth() - 1) {
//            CallStack oldStack = currentStack;
            depth++;
            LOGGER.debug("+ depth = " + depth + " method = " + psiMethod.getName());
//            offsetStack.push(offset);
            // 循环调用生成，显示它不是通过方法调用与退出来实现的
            generate(psiMethod);
            depth--;
            LOGGER.debug("- depth = " + depth + " method = " + psiMethod.getName());
//            currentStack = oldStack;
        } else {
//            currentStack.methodCall(createMethod(psiMethod, offset));
        }
    }

    private void variableImplementationFinder(PsiJavaCodeReferenceElement referenceElement, PsiType psiType, PsiExpression initializer) {
        if (referenceElement != null) {
            PsiClass psiClass = (PsiClass) referenceElement.resolve();

            if (SequenceOutlinePsiUtils.isAbstract(psiClass)) {
                String type = psiType.getCanonicalText();
                if (initializer instanceof PsiNewExpression) {
                    PsiType initializerType = initializer.getType();
                    if (initializerType != null) {
                        String impl = initializerType.getCanonicalText();
                        if (!type.equals(impl)) {
                            params.getInterfaceImplFilter().put(type, new ImplementClassFilter(impl));
                        }
                    }

                }
            }
        }
    }

    /**
     * 设置坐标值
     * 由于多线程的原因，col 只能表示列被添加的序列
     * 并不代表树型结构从左到右的顺序
     *
     * @param treeInvokeModel
     * @param row
     */
    private void fillRowCol(TreeInvokeModel treeInvokeModel, Integer row) {
        AtomicInteger atomicInteger = rowColIndexMap.get(row);
        if (null == atomicInteger) {
            atomicInteger = new AtomicInteger();
            rowColIndexMap.put(row, atomicInteger);
        }
        treeInvokeModel.setRow(row);
        treeInvokeModel.setCol(atomicInteger.getAndIncrement());
    }

    private class ImplementationFinder extends JavaElementVisitor {

        @Override
        public void visitClass(PsiClass aClass) {
            for (PsiClass psiClass : aClass.getSupers()) {
                if (!SequenceOutlinePsiUtils.isExternal(psiClass))
                    psiClass.accept(this);
            }

            if (!SequenceOutlinePsiUtils.isAbstract(aClass) && !SequenceOutlinePsiUtils.isExternal(aClass)) {
                super.visitClass(aClass);
            }
        }

        @Override
        public void visitField(PsiField field) {
            PsiTypeElement typeElement = field.getTypeElement();
            if (typeElement != null) {
                PsiJavaCodeReferenceElement referenceElement = typeElement.getInnermostComponentReferenceElement();
                variableImplementationFinder(referenceElement, field.getType(), field.getInitializer());
            }

            super.visitField(field);
        }

        @Override
        public void visitMethod(PsiMethod method) {
            // only constructor
            PsiClass containingClass = method.getContainingClass();
            if (containingClass != null && method.getName().equals(containingClass.getName())) {
                super.visitMethod(method);
            }
        }

        @Override
        public void visitAssignmentExpression(PsiAssignmentExpression expression) {
            PsiExpression re = expression.getRExpression();
            if (re instanceof PsiNewExpression) {
                String face = Objects.requireNonNull(expression.getType()).getCanonicalText();
                String impl = Objects.requireNonNull(expression.getRExpression().getType()).getCanonicalText();

                params.getInterfaceImplFilter().put(face, new ImplementClassFilter(impl));

            }
            super.visitAssignmentExpression(expression);
        }

        public void visitElement(PsiElement psiElement) {
            psiElement.acceptChildren(this);
        }

    }
}
