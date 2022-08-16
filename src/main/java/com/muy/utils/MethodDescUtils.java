package com.muy.utils;

import com.intellij.psi.*;
import net.sf.cglib.core.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class MethodDescUtils {

    private static final Map<String, String> transforms = new HashMap();
    private static final Map<String, String> rtransforms = new HashMap();

    static {
        transforms.put("void", "V");
        transforms.put("byte", "B");
        transforms.put("char", "C");
        transforms.put("double", "D");
        transforms.put("float", "F");
        transforms.put("int", "I");
        transforms.put("long", "J");
        transforms.put("short", "S");
        transforms.put("boolean", "Z");
        CollectionUtils.reverse(transforms, rtransforms);
    }


    /**
     * 1.先判断是不是数组
     * 2.哪种数组 int\boolean
     * 3.判断是否为泛型，去掉泛型
     */
    public static final Map<String, String> nameDescMap = Collections.unmodifiableMap(new HashMap<String, String>() {{
        put("void", "V");
        put("boolean", "Z");
        put("byte", "B");
        put("char", "C");
        put("short", "S");
        put("int", "I");
        put("long", "J");
        put("float", "F");
        put("double", "D");
        put("[]", "[");
        put("com.", "L;");
    }});

    /**
     * 1.可能是泛型,除去泛型
     * 2.如果包含数组
     *
     * @param canonicalText
     * @return
     */
    public static String signature(String canonicalText) {
        int pos = canonicalText.indexOf("<");
        // 如果泛型
        if (pos > 0) {
            canonicalText = canonicalText.substring(0, pos);
        }
        // todo 如果是数组
        String sig = transforms.get(canonicalText);
        if (StringUtils.isNotBlank(sig)) {
            return sig;
        }
        return classSig(canonicalText);
    }

    public static String classSig(String className) {
        return "L" + className.replace('.', '/') + ";";
    }

    /**
     * Returns the descriptor corresponding to the given method.
     *
     * @param m a {@link Method Method} object.
     * @return the descriptor of the given method.
     */
    // public
    public static String getMethodDescriptor(final PsiMethod psiMethod) {
        PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
        StringBuilder buf = new StringBuilder();
        buf.append('(');
        for (final PsiParameter parameter : parameters) {
            getDescriptor(buf, parameter.getType());
        }
        buf.append(')');
        getDescriptor(buf, psiMethod.getReturnType());
        return buf.toString();
    }

    /**
     * Returns the descriptor corresponding to the given method.
     *
     * @param m a {@link Method Method} object.
     * @return the descriptor of the given method.
     */
    // public
    public static String getMethodDescriptor(final PsiLambdaExpression expression) {
        PsiParameter[] parameters = expression.getParameterList().getParameters();
        StringBuilder buf = new StringBuilder();
        buf.append('(');
        for (final PsiParameter parameter : parameters) {
            getDescriptor(buf, parameter.getType());
        }
        buf.append(')');
        getDescriptor(buf, expression.getFunctionalInterfaceType());
        return buf.toString();
    }

    public static void getDescriptor(final StringBuilder buf, final PsiType psiType) {
        PsiType d = psiType;
        while (true) {
            if (d instanceof PsiPrimitiveType) {
                PsiPrimitiveType primitiveType = (PsiPrimitiveType) d;
                buf.append(primitiveType.getKind().getBinaryName());
                return;
            } else if (d instanceof PsiArrayType) {
                buf.append('[');
                d = d.getDeepComponentType();
            } else if (null == d) {
                // 如果是构造函数，则返回PsiType是空对象，目前签名是按V来处理, 注意一定要返回，否则死循环了
                buf.append('V');
                return;
            } else {
                buf.append('L');
                String name = removeGenerics(d.getCanonicalText());
                int len = name.length();
                boolean innerClass = false;
                for (int i = 0; i < len; ++i) {
                    char car = name.charAt(i);
                    if (car == '.') {
                        if (innerClass) {
                            buf.append('$');
                        } else {
                            buf.append('/');
                        }
                        int nextCar = i + 1;
                        if (nextCar < len) {
                            if (Character.isUpperCase(name.charAt(nextCar))) {
                                innerClass = true;
                            }
                        }
                    } else {
                        buf.append(car);
                    }
                }
                buf.append(';');
                return;
            }
        }
    }

    public static String removeGenerics(String paramTypeName) {
        int pos = paramTypeName.indexOf("<");
        // 如果泛型
        if (pos > 0) {
            paramTypeName = paramTypeName.substring(0, pos);
        }
        return paramTypeName;
    }
}
