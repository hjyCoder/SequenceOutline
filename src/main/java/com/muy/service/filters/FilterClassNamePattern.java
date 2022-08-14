package com.muy.service.filters;

import com.intellij.psi.PsiMethod;
import com.muy.utils.ReflectStringUtils;

import java.util.Set;

/**
 * @Author jiyanghuang
 * @Date 2022/7/4 01:16
 */
public class FilterClassNamePattern implements MethodFilter {

    /**
     * 类名正则表达式
     */
    private Set<String> classNamePattern;

    public FilterClassNamePattern(Set<String> classNamePattern) {
        this.classNamePattern = classNamePattern;
    }

    @Override
    public boolean allow(PsiMethod psiMethod) {
        if (null == psiMethod.getContainingClass() || null == psiMethod.getContainingClass().getQualifiedName()) {
            return false;
        }
        if (null == classNamePattern || 0 == classNamePattern.size()) {
            return true;
        }
        return regSet(classNamePattern, ReflectStringUtils.classSimpleName(psiMethod.getContainingClass().getQualifiedName()));
    }
}
