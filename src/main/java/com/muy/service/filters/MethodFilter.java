package com.muy.service.filters;

import com.intellij.psi.PsiMethod;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public interface MethodFilter {

    boolean allow(PsiMethod psiMethod);

    /**
     * 匹配某个正则表达式
     *
     * @param pattern
     * @param value
     * @return
     */
    public default boolean reg(String pattern, String value) {
        Pattern patternCompile = Pattern.compile(pattern);
        Matcher matcher = patternCompile.matcher(value);
        return matcher.find();
    }

    public default boolean regSet(Set<String> patterns, String value) {
        if (null == patterns || null == value) {
            return false;
        }

        for (String pattern : patterns) {
            if (reg(pattern, value)) {
                return true;
            }
        }
        return false;
    }

    public default boolean regSetExc(Set<String> patterns, String value) {
        if (null == patterns || null == value) {
            return false;
        }

        for (String pattern : patterns) {
            if (reg(pattern, value)) {
                return false;
            }
        }
        return true;
    }
}
