package com.muy.service.filters;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.muy.constant.SequenceConstant;
import com.muy.utils.SequenceOutlinePsiUtils;

import java.util.List;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class SingleMethodFilter implements MethodFilter {
    private String _className;
    private String _methodName;
    private List<String> _argTypes;

    public SingleMethodFilter(String className, String methodName, List<String> argTypes) {
        _className = className;
        _methodName = methodName;
        _argTypes = argTypes;
    }

    public boolean allow(PsiMethod psiMethod) {
        PsiClass containingClass = psiMethod.getContainingClass();
        if (isSameClass(containingClass) && SequenceOutlinePsiUtils.isMethod(psiMethod, _methodName, _argTypes))
            return false;
        return true;
    }

    private boolean isSameClass(PsiClass containingClass) {
        return _className.equals(SequenceConstant.ANONYMOUS_CLASS_NAME) &&
                containingClass.getQualifiedName() == null ||
                _className.equals(containingClass.getQualifiedName());
    }
}
