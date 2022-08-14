package com.muy.service.filters;

import com.intellij.psi.PsiMethod;
import com.muy.utils.ReflectStringUtils;

import java.util.Set;

/**
 * @Author jiyanghuang
 * @Date 2022/7/4 00:54
 */
public class FilterExcPackageNamePattern implements MethodFilter {

    /**
     * 排除哪些类不展示
     */
    private Set<String> exPackageNamePattern;

    public FilterExcPackageNamePattern(Set<String> exPackageNamePattern) {
        this.exPackageNamePattern = exPackageNamePattern;
    }

    @Override
    public boolean allow(PsiMethod psiMethod) {
        if (null == psiMethod.getContainingClass() || null == psiMethod.getContainingClass().getQualifiedName()) {
            return false;
        }
        if (null == exPackageNamePattern || 0 == exPackageNamePattern.size()) {
            return true;
        }
        return regSetExc(exPackageNamePattern, ReflectStringUtils.packageName(psiMethod.getContainingClass().getQualifiedName()));
    }
}
