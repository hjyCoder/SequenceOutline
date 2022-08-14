package com.muy.service.filters;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.muy.utils.SequenceOutlinePsiUtils;

import java.util.Set;

/**
 * @Author jiyanghuang
 * @Date 2022/7/4 00:49
 */
public class FilterParentClassNamePattern implements MethodFilter {

    /**
     * 允许某些类的子类
     * 必须全类名了
     */
    private Set<String> parentClassNamePattern;

    private Project project;

    public FilterParentClassNamePattern(Set<String> parentClassNamePattern, Project project) {
        this.parentClassNamePattern = parentClassNamePattern;
        this.project = project;
    }

    @Override
    public boolean allow(PsiMethod psiMethod) {
        if (null == psiMethod.getContainingClass() || null == psiMethod.getContainingClass().getQualifiedName()) {
            return false;
        }
        if (null == parentClassNamePattern || 0 == parentClassNamePattern.size()) {
            return true;
        }
        for(String parent : parentClassNamePattern){
            boolean find = SequenceOutlinePsiUtils.isInheritor(psiMethod.getContainingClass().getQualifiedName(), parent, project);
            if(find){
                return true;
            }
        }
        return false;
    }
}
