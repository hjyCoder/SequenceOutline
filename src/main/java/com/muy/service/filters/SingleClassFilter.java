package com.muy.service.filters;

import com.intellij.psi.PsiMethod;
import com.muy.constant.SequenceConstant;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class SingleClassFilter implements MethodFilter {
    private String _className;

    public SingleClassFilter(String className) {
        _className = className;
    }

    public boolean allow(PsiMethod psiMethod) {
        if (_className.equals(SequenceConstant.ANONYMOUS_CLASS_NAME) &&
                psiMethod.getContainingClass().getQualifiedName() == null)
            return false;
        if (psiMethod.getContainingClass().getQualifiedName() == null)
            return true;
        if (_className.equals(psiMethod.getContainingClass().getQualifiedName()))
            return false;
        return true;
    }
}
