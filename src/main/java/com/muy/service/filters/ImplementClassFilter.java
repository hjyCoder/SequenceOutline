package com.muy.service.filters;

import com.intellij.psi.PsiMethod;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class ImplementClassFilter extends SingleClassFilter {
    public ImplementClassFilter(String className) {
        super(className);
    }

    @Override
    public boolean allow(PsiMethod psiMethod) {
        return !super.allow(psiMethod);
    }
}
