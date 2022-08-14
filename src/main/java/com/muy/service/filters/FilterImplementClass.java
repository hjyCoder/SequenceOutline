package com.muy.service.filters;

import com.intellij.psi.PsiMethod;

import java.util.Set;

/**
 * @Author jiyanghuang
 * @Date 2022/7/4 22:50
 */
public class FilterImplementClass implements MethodFilter {

    private Set<String> implementClassSet;

    public FilterImplementClass(Set<String> implementClassSet) {
        this.implementClassSet = implementClassSet;
    }

    @Override
    public boolean allow(PsiMethod psiMethod) {
        if (null == psiMethod.getContainingClass() || null == psiMethod.getContainingClass().getQualifiedName()) {
            return false;
        }
        if (null == implementClassSet || 0 == implementClassSet.size()) {
            return false;
        }
        return regSet(implementClassSet, psiMethod.getContainingClass().getQualifiedName());
    }
}
