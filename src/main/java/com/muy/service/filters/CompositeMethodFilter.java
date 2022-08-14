package com.muy.service.filters;

import com.intellij.psi.PsiMethod;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class CompositeMethodFilter implements MethodFilter {
    private List<MethodFilter> _filters = new ArrayList<>();
    @Setter
    private PsiMethod entrance;

    public void addFilter(MethodFilter filter) {
        _filters.add(filter);
    }

    public void removeFilter(MethodFilter filter) {
        _filters.remove(filter);
    }

    @Override
    public boolean allow(PsiMethod psiMethod) {
        if(null != entrance){
            if(entrance == psiMethod){
                return true;
            }
        }
        for (MethodFilter methodFilter : _filters) {
            if (!methodFilter.allow(psiMethod)) {
                return false;
            }
        }
        return true;
    }

}
