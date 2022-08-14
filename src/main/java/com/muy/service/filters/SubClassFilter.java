package com.muy.service.filters;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.muy.utils.SequenceOutlinePsiUtils;

/**
 * @Author jiyanghuang
 * @Date 2022/7/3 22:58
 */
public class SubClassFilter implements MethodFilter {

    private String _className;
    private String entranceClassName;
    private Project project;

    public SubClassFilter(String _className, String entranceClassName, Project project) {
        this._className = _className;
        this.entranceClassName = entranceClassName;
        this.project = project;
    }

    @Override
    public boolean allow(PsiMethod psiMethod) {
        if (null == psiMethod.getContainingClass() || null == psiMethod.getContainingClass().getQualifiedName()) {
            return false;
        }
        if (psiMethod.getContainingClass().getQualifiedName().equals(entranceClassName)) {
            return true;
        }
        boolean inheritor = SequenceOutlinePsiUtils.isInheritor(psiMethod.getContainingClass().getQualifiedName(), _className, project);
        if (inheritor) {
            System.out.println("find");
        }
        return false;
    }
}
