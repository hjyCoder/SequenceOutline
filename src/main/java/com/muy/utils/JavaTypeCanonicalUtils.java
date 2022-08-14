package com.muy.utils;

import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiEllipsisType;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;

import java.util.Stack;

/**
 * @Author jiyanghuang
 * @Date 2022/5/28 23:41
 */
public class JavaTypeCanonicalUtils {

    public static String fetchDescriptor(final PsiType psiType) {
        StringBuilder buf = new StringBuilder();
        getDescriptor(buf, psiType);
        return buf.toString();
    }

    /**
     * 这里只是部分当成方法签名来实现
     * 使用 int 而不是
     * @param buf
     * @param psiType
     */
    public static void getDescriptor(final StringBuilder buf, final PsiType psiType) {
        PsiType d = psiType;
        boolean containArr = false;
        while (true) {
            if (d instanceof PsiPrimitiveType) {
                PsiPrimitiveType primitiveType = (PsiPrimitiveType) d;
                buf.append(primitiveType.getKind().getBinaryName());
                return;
            } else if (d instanceof PsiArrayType) {
                PsiArrayType psiArrayType = (PsiArrayType) d;
                buf.append('[');
                containArr = true;
                d = psiArrayType.getComponentType();
            } else if (null == d) {
                // 如果是构造函数，则返回PsiType是空对象，目前签名是按V来处理, 注意一定要返回，否则死循环了
                buf.append('V');
                return;
            } else {
                if (containArr) {
                    buf.append('L');
                }
                String name = canonicalTextToJavaType(d.getCanonicalText());
                buf.append(name);
                if (containArr) {
                    buf.append(';');
                }
                return;
            }
        }
    }

    /**
     * 临时解决方案，涉及复杂泛型可能会有问题
     *
     * @param canonicalText
     * @return
     */
    public static String canonicalTextToJavaType(String canonicalText) {
        int len = canonicalText.length();
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < len; ++i) {
            char car = canonicalText.charAt(i);
            if (car == '>') {
                StringBuilder sb = new StringBuilder();
                Integer deleteIndex = -1;
                sb.append(">");
                deleteIndex = sb.length();
                sb.append(";");
                String pStr = stack.pop();
                // 主要解决数组问题
                int arrCount = 0;
                while (!pStr.equals("<")) {
                    if (pStr.length() > 1) {
                        sb.append(pStr);
                    } else {
                        if (pStr.equals("]")) {
                            arrCount++;
                        } else if (pStr.equals(",")) {
                            if (arrCount > 0) {
                                sb.append("L");
                                for (int j = 0; j < arrCount; j++) {
                                    sb.append("[");
                                }
                                arrCount = 0;
                            } else {
                                if (deleteIndex > 0) {
                                    sb.deleteCharAt(deleteIndex);
                                }
                            }
                            sb.append(pStr);
                            deleteIndex = sb.length();
                            sb.append(";");
                        } else if (!pStr.equals("[")) {
                            sb.append(pStr);
                        }
                    }
                    pStr = stack.pop();
                }
                if (arrCount > 0) {
                    sb.append("L");
                    for (int j = 0; j < arrCount; j++) {
                        sb.append("[");
                    }
                } else {
                    if (deleteIndex > 0) {
                        sb.deleteCharAt(deleteIndex);
                    }
                }
                sb.append("<");
                stack.push(sb.toString());
            } else {
                stack.push(String.valueOf(car));
            }
        }
        StringBuilder residue = new StringBuilder();
        if (!stack.isEmpty()) {
            while (!stack.isEmpty()) {
                residue.append(stack.pop());
            }
        }
        return residue.reverse().toString();
    }
}
