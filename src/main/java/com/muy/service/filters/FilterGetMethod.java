package com.muy.service.filters;

import com.google.common.collect.Sets;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;

import java.util.Set;

/**
 * @Author jiyanghuang
 * @Date 2022/7/4 01:42
 */
public class FilterGetMethod implements MethodFilter {

    private Boolean notGet;

    private Set<String> allowGet;

    public FilterGetMethod(Boolean notGet, Set<String> allowGet) {
        this.notGet = notGet;
        this.allowGet = allowGet;
    }

    @Override
    public boolean allow(PsiMethod psiMethod) {
        if (null == psiMethod.getContainingClass() || null == psiMethod.getContainingClass().getQualifiedName()) {
            return false;
        }
        if (null == notGet || !notGet) {
            return true;
        }

        if (!psiMethod.getName().startsWith("get")) {
            return true;
        }

        // 方法可能以get开头，但不是get属性值，这里不区分大小写
        PsiField[] fields = psiMethod.getContainingClass().getFields();
        Set<String> fieldNameSet = Sets.newHashSet();
        for(PsiField psiField : fields){
            fieldNameSet.add("get" + psiField.getName().toLowerCase());
        }
        if (!fieldNameSet.contains(psiMethod.getName().toLowerCase())) {
            return true;
        }
        if (null == allowGet || 0 == allowGet.size()) {
            return false;
        }
        return regSet(allowGet, psiMethod.getName());
    }
}
