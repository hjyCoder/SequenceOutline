package com.muy.service.filters;

import com.intellij.psi.PsiMethod;

import java.util.Set;

/**
 * @Author jiyanghuang
 * @Date 2022/7/4 01:31
 */
public class FilterConstructor implements MethodFilter {

    private Boolean notConstructor;

    private Set<String> allowConstructor;

    public FilterConstructor(Boolean notConstructor, Set<String> allowConstructor) {
        this.notConstructor = notConstructor;
        this.allowConstructor = allowConstructor;
    }

    @Override
    public boolean allow(PsiMethod psiMethod) {
        if (null == psiMethod.getContainingClass() || null == psiMethod.getContainingClass().getQualifiedName()) {
            return false;
        }
        if (null == notConstructor || !notConstructor) {
            return true;
        }
        if (!psiMethod.isConstructor()) {
            return true;
        }
        if (null == allowConstructor || 0 == allowConstructor.size()) {
            return false;
        }
        return regSet(allowConstructor, psiMethod.getName());
    }
}
