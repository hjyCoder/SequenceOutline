package com.muy.service.filters;

import com.intellij.psi.PsiMethod;

/**
 * @Author jiyanghuang
 * @Date 2022/7/3 23:09
 */
public class FilterEntranceClass implements MethodFilter {

    private String _className;

    public FilterEntranceClass(String _className) {
        this._className = _className;
    }

    @Override
    public boolean allow(PsiMethod psiMethod) {
        if (null == psiMethod.getContainingClass() || null == psiMethod.getContainingClass().getQualifiedName()) {
            return false;
        }
        return psiMethod.getContainingClass().getQualifiedName().equals(_className);
    }
}
