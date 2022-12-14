package com.muy.service.filters;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PropertyUtil;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class NoGetterSetterFilter implements MethodFilter {
    private boolean _noGetterSetters = true;

    public NoGetterSetterFilter(boolean noGetterSetters) {
        _noGetterSetters = noGetterSetters;
    }

    public boolean isNoGetterSetters() {
        return _noGetterSetters;
    }

    public void setNoGetterSetters(boolean noGetterSetters) {
        _noGetterSetters = noGetterSetters;
    }

    public boolean allow(PsiMethod psiMethod) {
        if(_noGetterSetters && isGetterSetter(psiMethod))
            return false;
        return true;
    }

    private boolean isGetterSetter(PsiMethod psiMethod) {
        return PropertyUtil.isSimplePropertyGetter(psiMethod) ||
              PropertyUtil.isSimplePropertySetter(psiMethod);
    }
}
