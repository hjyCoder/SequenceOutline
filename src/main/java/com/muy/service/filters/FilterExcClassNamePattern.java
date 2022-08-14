package com.muy.service.filters;

import com.intellij.psi.PsiMethod;
import com.muy.utils.ReflectStringUtils;

import java.util.Set;

/**
 * @Author jiyanghuang
 * @Date 2022/7/4 00:58
 */
public class FilterExcClassNamePattern implements MethodFilter {

    /**
     * 排除哪些类不展示
     */
    private Set<String> exClassNamePattern;

    public FilterExcClassNamePattern(Set<String> exClassNamePattern) {
        this.exClassNamePattern = exClassNamePattern;
    }

    @Override
    public boolean allow(PsiMethod psiMethod) {
        if (null == psiMethod.getContainingClass() || null == psiMethod.getContainingClass().getQualifiedName()) {
            return false;
        }
        if (null == exClassNamePattern || 0 == exClassNamePattern.size()) {
            return true;
        }
        return regSetExc(exClassNamePattern, ReflectStringUtils.classSimpleName(psiMethod.getContainingClass().getQualifiedName()));
    }
}
