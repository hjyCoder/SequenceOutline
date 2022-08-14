package com.muy.service.filters;

import com.intellij.psi.PsiMethod;
import com.muy.utils.SequenceOutlinePsiUtils;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class PackageFilter implements MethodFilter {
    private String _packageName;
    private boolean _recursive;

    public PackageFilter(String packageName) {
        this(packageName, false);
    }

    public PackageFilter(String packageName, boolean recursive) {
        _packageName = packageName;
        _recursive = recursive;
    }

    public boolean allow(PsiMethod psiMethod) {
        String packageName = SequenceOutlinePsiUtils.getPackageName(psiMethod);
        if(packageName == null)
            return true;
        if (_recursive) {
            return !packageName.startsWith(_packageName);
        }
        else {
            return !packageName.equals(_packageName);
        }
    }
}
