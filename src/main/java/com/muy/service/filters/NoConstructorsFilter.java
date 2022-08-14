package com.muy.service.filters;

import com.intellij.psi.PsiMethod;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class NoConstructorsFilter implements MethodFilter {
    private boolean _noConstructors = false;

    public NoConstructorsFilter(boolean noConstructors) {
        _noConstructors = noConstructors;
    }

    public boolean isNoConstructors() {
        return _noConstructors;
    }

    public void setNoConstructors(boolean noConstructors) {
        _noConstructors = noConstructors;
    }

    public boolean allow(PsiMethod psiMethod) {
        if(_noConstructors && psiMethod.isConstructor())
            return false;
        return true;
    }
}
