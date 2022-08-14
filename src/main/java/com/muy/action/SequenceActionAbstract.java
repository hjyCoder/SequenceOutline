package com.muy.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.muy.common.utils.JacksonUtils;
import com.muy.domain.bean.invoketree.TreeInvokeModel;
import com.muy.service.SequenceGenerator;
import com.muy.service.SequenceParams;
import com.muy.service.filters.*;
import com.muy.service.filters.config.FilterConfig;
import com.muy.utils.ClipboardUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @Author jiyanghuang
 * @Date 2022/7/4 23:30
 */
public abstract class SequenceActionAbstract extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        PsiElement psiElement = event.getData(CommonDataKeys.PSI_ELEMENT);
        if (!(psiElement instanceof PsiMethod)) {
            return;
        }
        PsiMethod entrance = (PsiMethod)psiElement;
        String configJson = ClipboardUtils.fetchStringFromClip();
        List<FilterConfig> filterConfigList = JacksonUtils.toJavaObjectList(configJson, FilterConfig.class);
        if(null == filterConfigList || 2 != filterConfigList.size()){
            filterConfigList = defaultFilterConfig();
        }
        SequenceParams params = convertToSequenceParams(filterConfigList.get(0), entrance);

        SequenceGenerator sequenceGenerator = new SequenceGenerator(params);


        SequenceParams paramsStack = convertToSequenceParams(filterConfigList.get(1), entrance);

        sequenceGenerator.setParamsStack(paramsStack);
        sequenceGenerator.generate(psiElement);
        TreeInvokeModel root = sequenceGenerator.getRoot();
        genAfter(root);
    }

    protected void genAfter(TreeInvokeModel root){

    }

    private SequenceParams convertToSequenceParams(FilterConfig filterConfig, PsiMethod entrance){
        SequenceParams sequenceParams = new SequenceParams();
        sequenceParams.setMaxDepth(filterConfig.getCallDepth());
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
        return sequenceParams;
    }

    private List<FilterConfig> defaultFilterConfig(){
        String json = "[{\"notGet\":false,\"allowGet\":[],\"notSet\":false,\"allowSet\":[],\"notConstructor\":false,\"allowConstructor\":[],\"notPrivateMethod\":false,\"allowPrivateMethod\":[],\"methodNamePattern\":[],\"classNamePattern\":[],\"packageNamePattern\":[],\"exClassNamePattern\":[],\"exPackageNamePattern\":[\"java.\"],\"parentClassNamePattern\":[],\"callDepth\":100},{\"notGet\":false,\"allowGet\":[],\"notSet\":false,\"allowSet\":[],\"notConstructor\":false,\"allowConstructor\":[],\"notPrivateMethod\":false,\"allowPrivateMethod\":[],\"methodNamePattern\":[],\"classNamePattern\":[],\"packageNamePattern\":[],\"exClassNamePattern\":[],\"exPackageNamePattern\":[],\"parentClassNamePattern\":[],\"callDepth\":100}]";
        return JacksonUtils.toJavaObjectList(json, FilterConfig.class);
    }
}
