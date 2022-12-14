package com.muy.service.filters;

import com.intellij.psi.PsiMethod;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class NoPrivateMethodsFilter implements MethodFilter {
    private boolean _noPrivateMethods = false;

    public NoPrivateMethodsFilter(boolean noPrivateMethods) {
        _noPrivateMethods = noPrivateMethods;
    }

    public boolean isNoPrivateMethods() {
        return _noPrivateMethods;
    }

    public void setNoPrivateMethods(boolean noPrivateMethods) {
        _noPrivateMethods = noPrivateMethods;
    }

    public boolean allow(PsiMethod psiMethod) {
        if(_noPrivateMethods && isPrivateMethod(psiMethod))
            return false;
        return true;
    }

    private boolean isPrivateMethod(PsiMethod psiMethod) {
        return psiMethod.getModifierList().hasModifierProperty("private");
    }

}
