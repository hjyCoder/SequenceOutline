package com.muy.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.ClassUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.muy.service.FindLambdaVisitor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @Author jiyanghuang
 * @Date 2022/2/26 2:12 PM
 */
public class SequenceOutlinePsiUtils {
    private SequenceOutlinePsiUtils() {
    }

    public static PsiMethod getEnclosingMethod(PsiFile psiFile, int position) {
        PsiElement psiElement = psiFile.findElementAt(position);
        return PsiTreeUtil.getParentOfType(psiElement, PsiMethod.class);
    }

    public static boolean isInClassFile(PsiElement psiElement) {
        VirtualFile virtualFile = psiElement.getContainingFile().getVirtualFile();
        return virtualFile == null || virtualFile.getName().endsWith(".class");
    }

    public static boolean isInJarFileSystem(PsiElement psiElement) {
        if (psiElement == null)
            return false;
        VirtualFile virtualFile = psiElement.getContainingFile().getVirtualFile();
        if (virtualFile == null)
            return true;
        String protocol = virtualFile.getFileSystem().getProtocol();
        return protocol.equalsIgnoreCase("jar") || protocol.equalsIgnoreCase("zip");
    }

    public static VirtualFile findVirtualFile(PsiClass psiClass) {
        PsiFile containingFile = psiClass.getNavigationElement().getContainingFile();
        if (containingFile == null)
            return null;
        return containingFile.getVirtualFile();
    }

    public static PsiMethod findPsiMethod(PsiMethod[] psiMethods, String methodName, List<String> argTypes) {
        for (PsiMethod psiMethod : psiMethods) {
            if (SequenceOutlinePsiUtils.isMethod(psiMethod, methodName, argTypes))
                return psiMethod;
        }
        return null;
    }

    public static PsiMethod findPsiMethodSign(PsiMethod[] psiMethods, String methodName, String methodSign) {
        for (PsiMethod psiMethod : psiMethods) {
            if (SequenceOutlinePsiUtils.isMethod(psiMethod, methodName, methodSign))
                return psiMethod;
        }
        return null;
    }

    public static boolean isMethod(PsiMethod psiMethod, String methodName, List<String> argTypes) {
        if (!psiMethod.getName().equals(methodName))
            return false;
        PsiParameter[] psiParameters = psiMethod.getParameterList().getParameters();
        return parameterEquals(psiParameters, argTypes);
    }

    /**
     * 按方法签名来比较
     * @param psiMethod
     * @param methodName
     * @param methodSign
     * @return
     */
    public static boolean isMethod(PsiMethod psiMethod, String methodName, String methodSign) {
        if (!psiMethod.getName().equals(methodName)){
            return false;
        }
        String methodSignParser = MethodDescUtils.getMethodDescriptor(psiMethod);
        return methodSignParser.equals(methodSign);
    }

    public static boolean isAbstract(PsiClass psiClass) {
        return psiClass != null
                && (psiClass.isInterface()
                || psiClass.getModifierList() != null
                && psiClass.getModifierList().hasModifierProperty(PsiModifier.ABSTRACT)
        );
    }

    public static boolean isExternal(PsiClass psiClass) {
        return isInClassFile(psiClass) || isInJarFileSystem(psiClass);
    }

    public static PsiClass findPsiClass(PsiManager psiManager, String className) {
        return ClassUtil.findPsiClass(psiManager, className);
    }

    public static PsiMethod findPsiMethod(PsiManager psiManager,
                                          final String className, String methodName, List<String> argTypes) {
        PsiClass psiClass = ClassUtil.findPsiClass(psiManager, className);
        if (psiClass == null)
            return null;
        return findPsiMethod(psiClass, methodName, argTypes);
    }

    public static PsiMethod findPsiMethodSign(PsiManager psiManager,
                                          final String className, String methodName, String methodSign) {
        PsiClass psiClass = ClassUtil.findPsiClass(psiManager, className);
        if (psiClass == null)
            return null;
        return findPsiMethodSign(psiClass, methodName, methodSign);
    }

    @Nullable
    public static PsiMethod findPsiMethod(PsiClass psiClass, String methodName, List<String> argTypes) {
        PsiMethod[] psiMethods = psiClass.findMethodsByName(methodName, false);
        if (psiMethods.length == 0)
            return null;
        return SequenceOutlinePsiUtils.findPsiMethod(psiMethods, methodName, argTypes);
    }

    @Nullable
    public static PsiMethod findPsiMethodSign(PsiClass psiClass, String methodName, String methodSign) {
        PsiMethod[] psiMethods = psiClass.findMethodsByName(methodName, false);
        if (psiMethods.length == 0)
            return null;
        return SequenceOutlinePsiUtils.findPsiMethodSign(psiMethods, methodName, methodSign);
    }

    public static String getPackageName(PsiMethod psiMethod) {
        PsiElement psiElement = psiMethod.getParent();
        while (psiElement != null) {
            if (psiElement instanceof PsiJavaFile) {
                PsiJavaFile psiJavaFile = (PsiJavaFile) psiElement;
                return psiJavaFile.getPackageName();
            }
            psiElement = psiElement.getParent();
        }
        return null;
    }

    private static boolean parameterEquals(PsiParameter[] psiParameters, List<String> argTypes) {
        if (psiParameters.length != argTypes.size())
            return false;
        for (int i = 0; i < psiParameters.length; i++) {
            PsiParameter psiParameter = psiParameters[i];
            if (!psiParameter.getType().getCanonicalText().equals(argTypes.get(i)))
                return false;
        }
        return true;
    }

    /**
     * Find the best offset for navigation
     * @param psiElement PsiElement like PsiClass, PsiMethod, PsiCallExpression etc.
     * @return the offset.
     */
    public static int findNaviOffset(PsiElement psiElement) {
        if (psiElement == null)
            return 0;
        int offset;
        if (psiElement instanceof PsiMethodCallExpression) {
            offset = psiElement.getFirstChild().getNavigationElement().getTextOffset();
        } else {
            offset = psiElement.getNavigationElement().getTextOffset();
        }
        return offset;
    }

    /**
     * 匿名类也是 PsiClass， 所以匿名必须放在前
     *
     * @param psiElement
     * @return
     */
    public static PsiClass findPsiClass(PsiElement psiElement) {
        if (null == psiElement) {
            return null;
        }
        if (psiElement instanceof PsiAnonymousClass) {
            return findPsiClass(psiElement.getParent());
        } else if (psiElement instanceof PsiClass) {
            return (PsiClass) psiElement;
        } else {
            return findPsiClass(psiElement.getParent());
        }
    }

    public static void parsePsiLiteral(List<String> literals, PsiMethodCallExpression psiMethodCallExpression) {
        PsiElement[] childrens = psiMethodCallExpression.getChildren();
        for (PsiElement children : childrens) {
            if (children instanceof PsiExpressionList) {
                PsiExpressionList psiExpressionList = (PsiExpressionList) children;
                PsiElement[] cs = psiExpressionList.getChildren();
                for (PsiElement c : cs) {
                    if (c instanceof PsiMethodCallExpression) {
                        parsePsiLiteral(literals, (PsiMethodCallExpression) c);
                    } else if (c instanceof PsiLiteralExpression) {
                        PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) c;
                        String literal = psiLiteralExpression.getText();
                        int start = 0;
                        if(literal.startsWith("\"")){
                            start = start + 1;
                        }
                        int end = literal.length();
                        if(literal.endsWith("\"")){
                            end = end - 1;
                        }
                        literals.add(literal.substring(start, end));
                    }
                }
            }
        }
    }

    public static PsiMethod findParentPsiMethod(PsiElement parent) {
        if (null == parent) {
            return null;
        }
        if (parent instanceof PsiMethod) {
            return (PsiMethod) parent;
        } else {
            return findParentPsiMethod(parent.getParent());
        }
    }

    /**
     *
     * @param project
     * @param name
     * @return
     */
    public static PsiClass findPsiClass(Project project, String name){
        return ClassUtil.findPsiClass(psiManager(project), name);
    }

    public static PsiManager psiManager(Project project){
        return PsiManager.getInstance(project);
    }

    /**
     * 是否继承某个类或接口
     *
     * @param psiClass
     * @param clazz
     * @param project
     * @return
     */
    public static boolean isInheritor(PsiClass psiClass, Class<?> clazz, Project project) {
        if (psiClass.getQualifiedName().equals(clazz.getName())) {
            return false;
        }
        PsiClass psiClassParent = findPsiClass(project, clazz.getName());
        if (psiClassParent.isInterface()) {
            return psiClass.isInheritor(psiClassParent, true);
        }
        PsiClass psiClassP = psiClass.getSuperClass();
        while (!psiClassP.getQualifiedName().equals(Object.class.getName())) {
            if (psiClassP.getQualifiedName().equals(psiClassParent.getQualifiedName())) {
                return true;
            }
            psiClassP = psiClassP.getSuperClass();
        }
        return false;
    }

    /**
     * 是否继承某个类或接口
     *
     * @param psiClass
     * @param clazz
     * @param project
     * @return
     */
    public static boolean isInheritor(String fClassName, Class<?> clazz, Project project) {
        if (fClassName.equals(clazz.getName())) {
            return false;
        }
        PsiClass psiClass = findPsiClass(project, fClassName);
        PsiClass psiClassParent = findPsiClass(project, clazz.getName());
        if (psiClassParent.isInterface()) {
            return psiClass.isInheritor(psiClassParent, true);
        }
        PsiClass psiClassP = psiClass.getSuperClass();
        while (!psiClassP.getQualifiedName().equals(Object.class.getName())) {
            if (psiClassP.getQualifiedName().equals(psiClassParent.getQualifiedName())) {
                return true;
            }
            psiClassP = psiClassP.getSuperClass();
        }
        return false;
    }

    /**
     * 是否继承某个类或接口
     *
     * @param psiClass
     * @param clazz
     * @param project
     * @return
     */
    public static boolean isInheritor(String fClassName, String fClassNameParent, Project project) {
        if (fClassName.equals(fClassNameParent)) {
            return true;
        }
        PsiClass psiClass = findPsiClass(project, fClassName);
        PsiClass psiClassParent = findPsiClass(project, fClassNameParent);
        if (psiClassParent.isInterface()) {
            return psiClass.isInheritor(psiClassParent, true);
        }
        PsiClass psiClassP = psiClass.getSuperClass();
        while (!psiClassP.getQualifiedName().equals(Object.class.getName())) {
            if (psiClassP.getQualifiedName().equals(psiClassParent.getQualifiedName())) {
                return true;
            }
            psiClassP = psiClassP.getSuperClass();
        }
        return false;
    }

    public static PsiMethod findEnclosedPsiMethod(PsiLambdaExpression expression) {
        PsiElement parent = expression.getParent();
        while (!(parent instanceof PsiMethod)) {
            parent = parent.getParent();
        }
        return (PsiMethod) parent;
    }

    public static PsiLambdaExpression findLambda(PsiMethod psiMethod, String methodSignatrue, int pos) {
        FindLambdaVisitor findLambdaVisitor = new FindLambdaVisitor(methodSignatrue, psiMethod);
        findLambdaVisitor.startVisit();
        if (CollectionUtils.isEmpty(findLambdaVisitor.getFindResult())) {
            return null;
        }
        if (pos < 0 || pos >= findLambdaVisitor.getFindResult().size()) {
            return findLambdaVisitor.getFindResult().get(0);
        }
        return findLambdaVisitor.getFindResult().get(pos);
    }

    public static boolean superInvoke(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        return text.startsWith("super.") || text.contains(".super.");
    }
}
