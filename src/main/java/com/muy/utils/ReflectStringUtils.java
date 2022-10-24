package com.muy.utils;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.muy.common.exception.SequenceOutlineException;

/**
 * @Author jiyanghuang
 * @Date 2022/5/26 01:44
 */
public class ReflectStringUtils {

    public static String classSimpleName(String classFullName) {
        int lastIndex = classFullName.lastIndexOf(".");
        if (lastIndex < 0) {
            return classFullName;
        }
        return classFullName.substring(lastIndex + 1, classFullName.length());
    }

    public static String packageName(String classFullName) {
        int firstIndex = classFullName.indexOf("<");
        if (firstIndex > 0) {
            classFullName = classFullName.substring(0, firstIndex);
        }
        int lastIndex = classFullName.lastIndexOf(".");
        if (lastIndex < 0) {
            return "";
        }
        return classFullName.substring(0, lastIndex);
    }

    public static String canonicalSimpleName(String javaTypeCanonical) {
        int firstIndex = javaTypeCanonical.indexOf("<");
        if (firstIndex < 0) {
            return javaTypeCanonical;
        }
        return javaTypeCanonical.substring(0, firstIndex);
    }

    public static String beanName(String simpleClassName) {
        simpleClassName = classSimpleName(simpleClassName);
        return simpleClassName.substring(0, 1).toLowerCase() + simpleClassName.substring(1, simpleClassName.length());
    }

    public static String methodNameDesc(String methodBodyString) {
        int index = methodBodyString.indexOf('{');
        if (index <= 0) {
            return methodBodyString;
        }
        return methodBodyString.substring(0, index);
    }

    public static String methodNameDesc(PsiMethod psiMethod) {
        StringBuilder sb = new StringBuilder();
        sb.append(psiMethod.getName());
        sb.append("(");
        PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
        for (PsiParameter parameter : parameters) {
            sb.append(parameter.getType().getPresentableText());
            sb.append(" ");
            sb.append(parameter.getName());
            sb.append(", ");
        }
        if(parameters.length > 0){
            sb.delete(sb.length() - 2, sb.length() - 1);
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * 根据类文件找出类名
     * @param filePath
     * @return
     */
    public static String fulClassName(String filePath){
        String srcFile = "/src/main/java/";
        int index = filePath.indexOf(srcFile);
        if(index <= 0){
            throw new SequenceOutlineException("invalid filePath");
        }
        return filePath.substring(index + srcFile.length(), filePath.length() - ".java".length()).replaceAll("/", ".");
    }
}
