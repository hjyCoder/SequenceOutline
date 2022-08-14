package com.muy.service.filters;

import com.google.common.collect.Sets;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;

import java.util.Set;

/**
 * @Author jiyanghuang
 * @Date 2022/7/4 01:37
 */
public class FilterSetMethod implements MethodFilter{

    private Boolean notSet;

    private Set<String> allowSet;

    public FilterSetMethod(Boolean notSet, Set<String> allowSet) {
        this.notSet = notSet;
        this.allowSet = allowSet;
    }

    @Override
    public boolean allow(PsiMethod psiMethod) {
        if (null == psiMethod.getContainingClass() || null == psiMethod.getContainingClass().getQualifiedName()) {
            return false;
        }
        if (null == notSet || !notSet) {
            return true;
        }

        if (!psiMethod.getName().startsWith("set")) {
            return true;
        }
        // 方法可能以set开头，但不是set属性值，这里不区分大小写
        PsiField[] fields = psiMethod.getContainingClass().getFields();
        Set<String> fieldNameSet = Sets.newHashSet();
        for(PsiField psiField : fields){
            fieldNameSet.add("set" + psiField.getName().toLowerCase());
        }
        if (!fieldNameSet.contains(psiMethod.getName().toLowerCase())) {
            return true;
        }
        if (null == allowSet || 0 == allowSet.size()) {
            return false;
        }
        return regSet(allowSet, psiMethod.getName());
    }
}
