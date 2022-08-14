package com.muy.domain.bean.invoketree;


import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.muy.common.tree.enums.MethodType;
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

    public static TreeNodeModel of(PsiMethod psiMethod){
        TreeNodeModel treeNodeModel = new TreeNodeModel();
        treeNodeModel.setScheme("java");
        String fClassName;
        if(psiMethod.getContainingClass() instanceof PsiAnonymousClass){
            PsiClass psiClass = SequenceOutlinePsiUtils.findPsiClass(psiMethod.getContainingClass());
            fClassName = psiClass.getQualifiedName() + "$";
        }else{
            methodType(treeNodeModel, psiMethod);
            treeNodeModel.setMethodType(psiMethod.getContainingClass().isInterface() ? MethodType.INTERFACE_METHOD.getType() : MethodType.ABSTRACT_METHOD.getType());
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
        treeNodeModel.setMethodName(psiMethod.isConstructor() ? "<init>" : psiMethod.getName());
        treeNodeModel.setMethodSignature(MethodDescUtils.getMethodDescriptor(psiMethod));
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
}
