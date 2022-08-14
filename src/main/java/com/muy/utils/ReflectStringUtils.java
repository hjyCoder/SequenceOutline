package com.muy.utils;

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

    public static void main(String[] args) {
        System.out.println(ReflectStringUtils.packageName("top.idwangmo.axondemo.service.A"));
        System.out.println(ReflectStringUtils.packageName("A"));
        System.out.println(ReflectStringUtils.packageName("java.util.List<org.axonframework.commandhandling.gateway.CommandGateway<>>"));
    }
}
