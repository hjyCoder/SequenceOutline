package com.muy.service.filters;

import com.intellij.psi.PsiMethod;

import java.util.Set;

/**
 * @Author jiyanghuang
 * @Date 2022/7/4 01:18
 */
public class FilterMethodNamePattern implements MethodFilter {

    /**
     * 方法名正则表达式
     */
    private Set<String> methodNamePattern;

    public FilterMethodNamePattern(Set<String> methodNamePattern) {
        this.methodNamePattern = methodNamePattern;
    }

    @Override
    public boolean allow(PsiMethod psiMethod) {
        if (null == psiMethod.getContainingClass() || null == psiMethod.getContainingClass().getQualifiedName()) {
            return false;
        }
        if (null == methodNamePattern || 0 == methodNamePattern.size()) {
            return true;
        }
        return regSet(methodNamePattern, psiMethod.getName());
    }
}
