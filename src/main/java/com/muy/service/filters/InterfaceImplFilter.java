package com.muy.service.filters;

import com.intellij.psi.PsiMethod;

import java.util.HashMap;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class InterfaceImplFilter implements MethodFilter {

    private HashMap<String, MethodFilter> filters = new HashMap<>();

    public void clear() {
        filters.clear();
    }

    public void put(String key, MethodFilter filter) {
        filters.put(key, filter);
    }

    public MethodFilter get(String key) {
        return filters.get(key);
    }

    @Override
    public boolean allow(PsiMethod psiMethod) {
        for (MethodFilter filter : filters.values()) {
            if(filter.allow(psiMethod)) {
                return true;
            }
        }
        return false;
    }
}
