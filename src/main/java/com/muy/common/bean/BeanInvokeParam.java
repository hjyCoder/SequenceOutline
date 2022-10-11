package com.muy.common.bean;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.muy.common.dialog.DialogFormConstructorSelect;
import com.muy.common.dialog.MRDialog;
import com.muy.utils.JavaTypeCanonicalUtils;
import com.muy.utils.PsiTypeToKvForJsonUtils;
import com.muy.utils.ReflectStringUtils;
import lombok.Data;

import java.util.Map;

/**
 * @Author jiyanghuang
 * @Date 2022/5/26 00:59
 */
@Data
public class BeanInvokeParam {

    /**
     * 调用的全类名
     */
    private String classFullName;

    /**
     * 需要调用的 bean 名称
     */
    private String beanName;

    /**
     * 如果是存在Context则按beanName来调用
     * 否则新生成一个对象
     */
    private int invokeType;

    /**
     * 构建函数描述
     */
    private BeanInvokeMethodDesc constructorMethod;

    /**
     * 普通函数描述
     */
    private BeanInvokeMethodDesc method;

    /**
     * 生成Bean调用参数
     * @param psiMethod
     * @return
     */
    public static BeanInvokeParam ofOnlyMethod(PsiMethod psiMethod) {
        BeanInvokeParam beanInvokeParam = new BeanInvokeParam();

        String fClassName = psiMethod.getContainingClass().getQualifiedName();
        beanInvokeParam.setClassFullName(fClassName);
        beanInvokeParam.setInvokeType(BeanInvokeType.ONLY_METHOD.getCode());
        beanInvokeParam.setBeanName(ReflectStringUtils.beanName(fClassName));

        BeanInvokeMethodDesc methodDesc = new BeanInvokeMethodDesc();
        methodDesc.setMethodName(psiMethod.getName());

        PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
        if (null != parameters && parameters.length > 0) {
            methodDesc.setMpjtcs(Lists.newArrayListWithExpectedSize(parameters.length));
            for (PsiParameter parameter : parameters) {
                methodDesc.getMpjtcs().add(JavaTypeCanonicalUtils.fetchDescriptor(parameter.getType()));
            }
        }

        beanInvokeParam.setMethod(methodDesc);
        return beanInvokeParam;
    }

    /**
     * 生成Bean调用参数
     * @param psiMethod
     * @return
     */
    public static BeanInvokeParam ofInvokeMethod(PsiMethod psiMethod, BeanInvokeType beanInvokeType) {
        BeanInvokeParam beanInvokeParam = new BeanInvokeParam();

        String fClassName = psiMethod.getContainingClass().getQualifiedName();
        beanInvokeParam.setClassFullName(fClassName);
        beanInvokeParam.setInvokeType(beanInvokeType.getCode());
        // 静态方法不需要生成对象
        if(psiMethod.getModifierList().getText().contains("static")){
            beanInvokeParam.setInvokeType(BeanInvokeType.STATIC_METHOD.getCode());
        }
        if(BeanInvokeType.ONLY_METHOD.equals(beanInvokeType)){
            beanInvokeParam.setBeanName(ReflectStringUtils.beanName(fClassName));
        }else if(BeanInvokeType.CONSTRUCT_INVOKE_METHOD.equals(beanInvokeType)){
            PsiMethod[] psiMethodConstructors = psiMethod.getContainingClass().getConstructors();
            if(null == psiMethodConstructors || 0 == psiMethodConstructors.length){
                beanInvokeParam.setConstructorMethod(BeanInvokeMethodDesc.defaultConstructor());
            }else{
                Map<String, PsiMethod> psiMethodMap = Maps.newHashMap();
                for(PsiMethod psiMethodConstructor : psiMethodConstructors){
                    psiMethodMap.put(ReflectStringUtils.methodNameDesc(psiMethodConstructor.getText()), psiMethodConstructor);
                }
                DialogFormConstructorSelect dialogFormConstructorSelect = new DialogFormConstructorSelect(Lists.newArrayList(psiMethodMap.keySet()), (methodSign) -> {
                    PsiMethod psiMethodSelect = psiMethodMap.get(methodSign);
                    beanInvokeParam.setConstructorMethod(convertTo(psiMethodSelect));
                });
                MRDialog.of(dialogFormConstructorSelect).show();
            }
        }

        beanInvokeParam.setMethod(convertTo(psiMethod));
        return beanInvokeParam;
    }

    public static BeanInvokeMethodDesc convertTo(PsiMethod psiMethod) {
        BeanInvokeMethodDesc methodDesc = new BeanInvokeMethodDesc();
        methodDesc.setMethodName(psiMethod.getName());
        if (psiMethod.isConstructor()) {
            methodDesc.setMethodName(BeanInvokeMethodDesc.CONSTRUCTOR_METHOD_NAME);
        }

        PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
        if (null != parameters && parameters.length > 0) {
            methodDesc.setMpjtcs(Lists.newArrayListWithExpectedSize(parameters.length));
            methodDesc.setMpjtcsValueJson(Lists.newArrayListWithExpectedSize(parameters.length));
            for (PsiParameter parameter : parameters) {
                methodDesc.getMpjtcs().add(JavaTypeCanonicalUtils.fetchDescriptor(parameter.getType()));
                methodDesc.getMpjtcsValueJson().add(PsiTypeToKvForJsonUtils.methodParamJson(parameter.getType()));
            }
        }
        return methodDesc;
    }
}
