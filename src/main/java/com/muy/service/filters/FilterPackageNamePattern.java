package com.muy.service.filters;

import com.intellij.psi.PsiMethod;
import com.muy.utils.ReflectStringUtils;

import java.util.Set;

/**
 * @Author jiyanghuang
 * @Date 2022/7/4 01:13
 */
public class FilterPackageNamePattern implements MethodFilter {

    /**
     * 包名正则表达式
     */
    private Set<String> packageNamePattern;

    public FilterPackageNamePattern(Set<String> packageNamePattern) {
        this.packageNamePattern = packageNamePattern;
    }

    @Override
    public boolean allow(PsiMethod psiMethod) {
        if (null == psiMethod.getContainingClass() || null == psiMethod.getContainingClass().getQualifiedName()) {
            return false;
        }
        if (null == packageNamePattern || 0 == packageNamePattern.size()) {
            return true;
        }
        return regSet(packageNamePattern, ReflectStringUtils.packageName(psiMethod.getContainingClass().getQualifiedName()));
    }
}
