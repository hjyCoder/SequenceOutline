package com.muy.domain.bean.invoketree;


import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.muy.common.tree.enums.MethodType;
import com.muy.constant.SequenceConstant;
import com.muy.utils.MethodDescUtils;
import com.muy.utils.SequenceOutlinePsiUtils;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
@Data
public class TreeNodeModel implements Serializable, Cloneable {

    public static final String CONSTRUCTORS_METHOD_NAME = "<init>";

    public static final String SCHEME_JAVA = "java";

    public static final String SCHEME_LAMBDA = "lambda";

    private String scheme;

    private String packageName;

    private String className;

    private String methodName;

    private String methodSignature;

    private String uri;

    private String uriMd5;

    /**
     * 默认是普通方法
     * 用于静态扫描时显示图片用
     */
    private int methodType = 2;

    /**
     * 当是 lambda 时，所在方法的签名，用于跳转
     */
    private String encloseMethodSignature;


    public String uriMd5Gen() {
        // java://className/methodName~(methodSign)reSign
        StringBuilder sb = new StringBuilder();
        sb.append(scheme).append("://");
        if (StringUtils.isNotBlank(packageName)) {
            sb.append(packageName).append(".").append(className).append("/");
        } else {
            sb.append(className).append("/");
        }
        sb.append(methodName).append("~");
        sb.append(methodSignature);
        uri = sb.toString();
        uriMd5 = DigestUtils.md5Hex(uri);
        return uriMd5;
    }

    public String getUriMd5() {
        if(StringUtils.isBlank(uriMd5)){
            uriMd5 = uriMd5Gen();
        }
        return uriMd5;
    }

    /**
     * 判断是否为相同的 Lambda, 包括包装的方法
     *
     * @param treeNodeModel
     * @return
     */
    public boolean sameLambda(TreeNodeModel treeNodeModel) {
        if (SCHEME_LAMBDA.equals(treeNodeModel.getScheme()) && SCHEME_LAMBDA.equals(this.getScheme())) {
            if (this.getMethodName().equals(treeNodeModel.getMethodName()) && this.getEncloseMethodSignature().equals(treeNodeModel.getEncloseMethodSignature())) {
                return true;
            }
        }
        return false;
    }

    public static TreeNodeModel of(PsiMethod psiMethod){
        TreeNodeModel treeNodeModel = new TreeNodeModel();
        treeNodeModel.setScheme("java");
        String fClassName;
        if(psiMethod.getContainingClass() instanceof PsiAnonymousClass){
            PsiClass psiClass = SequenceOutlinePsiUtils.findPsiClass(psiMethod.getContainingClass());
            fClassName = psiClass.getQualifiedName() + "$";
        }else{
            methodType(treeNodeModel, psiMethod);
            fClassName = psiMethod.getContainingClass().getQualifiedName();
        }
        int pos = fClassName.lastIndexOf(".");
        if (pos <= 0) {
            treeNodeModel.setClassName(fClassName);
            treeNodeModel.setPackageName("");
        } else {
            treeNodeModel.setClassName(fClassName.substring(pos + 1));
            treeNodeModel.setPackageName(fClassName.substring(0, pos));
        }
        treeNodeModel.setMethodName(psiMethod.isConstructor() ? CONSTRUCTORS_METHOD_NAME : psiMethod.getName());
        treeNodeModel.setMethodSignature(MethodDescUtils.getMethodDescriptor(psiMethod));
        String uriMd5 = treeNodeModel.uriMd5Gen();

        return treeNodeModel;
    }

    public static TreeNodeModel ofLambda(PsiLambdaExpression expression) {
        TreeNodeModel treeNodeModel = new TreeNodeModel();
        treeNodeModel.setScheme("lambda");
        String fClassName;
        PsiMethod psiMethod = SequenceOutlinePsiUtils.findEnclosedPsiMethod(expression);
        if (psiMethod.getContainingClass() instanceof PsiAnonymousClass) {
            PsiClass psiClass = SequenceOutlinePsiUtils.findPsiClass(psiMethod.getContainingClass());
            fClassName = psiClass.getQualifiedName() + "$";
        } else {
            methodType(treeNodeModel, psiMethod);
            fClassName = psiMethod.getContainingClass().getQualifiedName();
        }
        int pos = fClassName.lastIndexOf(".");
        if (pos <= 0) {
            treeNodeModel.setClassName(fClassName);
            treeNodeModel.setPackageName("");
        } else {
            treeNodeModel.setClassName(fClassName.substring(pos + 1));
            treeNodeModel.setPackageName(fClassName.substring(0, pos));
        }
        String functionName = "";
        PsiType functionNameType = expression.getFunctionalInterfaceType();
        if (null != functionNameType && functionNameType instanceof PsiClassReferenceType) {
            PsiClassReferenceType functionNameClass = (PsiClassReferenceType) functionNameType;
            functionName = "[" + functionNameClass.getClassName() + "]";
        }
        treeNodeModel.setMethodName(psiMethod.getName() + "_" + functionName + SequenceConstant.Lambda_Invoke);
        treeNodeModel.setMethodSignature(MethodDescUtils.getMethodDescriptor(expression));
        treeNodeModel.setEncloseMethodSignature(MethodDescUtils.getMethodDescriptor(psiMethod));
        treeNodeModel.setMethodType(MethodType.LAMBDA_METHOD.getType());
        String uriMd5 = treeNodeModel.uriMd5Gen();
        return treeNodeModel;
    }

    private static void methodType(TreeNodeModel treeNodeModel, PsiMethod psiMethod){
        if(psiMethod.getContainingClass().isInterface()){
            treeNodeModel.setMethodType(MethodType.INTERFACE_METHOD.getType());
        }else if(SequenceOutlinePsiUtils.isAbstract(psiMethod.getContainingClass())){
            treeNodeModel.setMethodType(MethodType.ABSTRACT_METHOD.getType());
        }
    }

    public String fClassName(){
        return packageName + "." + className;
    }

    public String findMethodName() {
        if (CONSTRUCTORS_METHOD_NAME.equals(methodName)) {
            return className;
        }
        return methodName;
    }

    public static boolean jsonConvertCheck(TreeNodeModel treeNodeModel) {
        if (null == treeNodeModel) {
            return false;
        }

        if (StringUtils.isBlank(treeNodeModel.getClassName())
                || StringUtils.isBlank(treeNodeModel.getMethodName())
                || StringUtils.isBlank(treeNodeModel.getMethodSignature())) {
            return false;
        }
        return true;
    }
}
