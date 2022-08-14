package com.muy.service.filters;

import com.intellij.psi.PsiMethod;
import com.muy.utils.SequenceOutlinePsiUtils;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class ProjectOnlyFilter implements MethodFilter {
    private boolean _projectClasssesOnly = true;

    public ProjectOnlyFilter(boolean projectClasssesOnly) {
        _projectClasssesOnly = projectClasssesOnly;
    }

    public boolean isProjectClasssesOnly() {
        return _projectClasssesOnly;
    }

    public void setProjectClasssesOnly(boolean projectClasssesOnly) {
        _projectClasssesOnly = projectClasssesOnly;
    }

    public boolean allow(PsiMethod psiMethod) {
        if (_projectClasssesOnly && isInProject(psiMethod))
            return false;
        return true;
    }

    private boolean isInProject(PsiMethod psiMethod) {
        return SequenceOutlinePsiUtils.isInJarFileSystem(psiMethod) || SequenceOutlinePsiUtils.isInClassFile(psiMethod);
    }

}
