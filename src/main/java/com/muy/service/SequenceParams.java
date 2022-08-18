package com.muy.service;

import com.intellij.psi.PsiMethod;
import com.muy.service.filters.*;
import com.muy.service.filters.config.FilterConfig;
import com.muy.utils.BooleanUtils;
import lombok.Data;

/**
 * @Author jiyanghuang
 * @Date 2022/6/24 00:50
 */
@Data
public class SequenceParams {

    private static final String PACKAGE_INDICATOR = ".*";
    private static final String RECURSIVE_PACKAGE_INDICATOR = ".**";

    private int maxDepth = 3;
    private boolean allowRecursion = false;
    private boolean smartInterface = true;
    private boolean notLambda = false;
    private final CompositeMethodFilter methodFilter = new CompositeMethodFilter();
    private final InterfaceImplFilter interfaceImplFilter = new InterfaceImplFilter();

    public static SequenceParams convertToSequenceParams(FilterConfig filterConfig, PsiMethod entrance){
        SequenceParams sequenceParams = new SequenceParams();
        sequenceParams.setMaxDepth(null == filterConfig.getCallDepth() ? 100 : filterConfig.getCallDepth());
        CompositeMethodFilter methodFilter = sequenceParams.getMethodFilter();
        methodFilter.setEntrance(entrance);
        methodFilter.addFilter(new FilterGetMethod(filterConfig.getNotGet(), filterConfig.getAllowGet()));
        methodFilter.addFilter(new FilterSetMethod(filterConfig.getNotSet(), filterConfig.getAllowSet()));
        methodFilter.addFilter(new FilterConstructor(filterConfig.getNotConstructor(), filterConfig.getAllowConstructor()));
        methodFilter.addFilter(new FilterPrivateMethod(filterConfig.getNotPrivateMethod(), filterConfig.getAllowPrivateMethod()));
        methodFilter.addFilter(new FilterMethodNamePattern(filterConfig.getMethodNamePattern()));
        methodFilter.addFilter(new FilterClassNamePattern(filterConfig.getClassNamePattern()));
        methodFilter.addFilter(new FilterPackageNamePattern(filterConfig.getPackageNamePattern()));
        methodFilter.addFilter(new FilterExcClassNamePattern(filterConfig.getExClassNamePattern()));
        methodFilter.addFilter(new FilterExcPackageNamePattern(filterConfig.getExPackageNamePattern()));
        methodFilter.addFilter(new FilterParentClassNamePattern(filterConfig.getParentClassNamePattern(), entrance.getProject()));

        InterfaceImplFilter interfaceImplFilter = sequenceParams.getInterfaceImplFilter();
        interfaceImplFilter.put("one", new FilterImplementClass(filterConfig.getImplementClassSet()));
        sequenceParams.setNotLambda(BooleanUtils.defaultFalse(filterConfig.getNotLambda()));
        return sequenceParams;
    }
}
