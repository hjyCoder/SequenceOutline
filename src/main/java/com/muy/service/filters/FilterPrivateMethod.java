package com.muy.service.filters;

import com.intellij.psi.PsiMethod;

import java.util.Set;

/**
 * @Author jiyanghuang
 * @Date 2022/7/4 01:22
 */
public class FilterPrivateMethod implements MethodFilter {

    private Boolean notPrivateMethod;

    private Set<String> allowPrivateMethod;

    public FilterPrivateMethod(Boolean notPrivateMethod, Set<String> allowPrivateMethod) {
        this.notPrivateMethod = notPrivateMethod;
        this.allowPrivateMethod = allowPrivateMethod;
    }

    @Override
    public boolean allow(PsiMethod psiMethod) {
        if (null == psiMethod.getContainingClass() || null == psiMethod.getContainingClass().getQualifiedName()) {
            return false;
        }
        if (null == notPrivateMethod || !notPrivateMethod) {
            return true;
        }

        if (!psiMethod.isConstructor()) {
            return true;
        }
        if (null == allowPrivateMethod || 0 == allowPrivateMethod.size()) {
            return false;
        }
        return regSet(allowPrivateMethod, psiMethod.getName());
    }

}
