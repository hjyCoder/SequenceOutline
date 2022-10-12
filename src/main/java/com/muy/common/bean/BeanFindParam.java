package com.muy.common.bean;

import com.intellij.psi.PsiField;
import com.muy.utils.ReflectStringUtils;
import lombok.Data;

/**
 * @Author jiyanghuang
 * @Date 2022/10/12 19:47
 */
@Data
public class BeanFindParam {
    /**
     * 调用的全类名
     */
    private String classFullName;

    /**
     * 需要调用的 bean 名称
     */
    private String beanName;

    /**
     * 需要调用的 bean 名称
     */
    private String fieldName;

    public static BeanFindParam of(PsiField psiField){
        BeanFindParam beanFindParam = new BeanFindParam();
        String fClassName = psiField.getContainingClass().getQualifiedName();
        beanFindParam.setClassFullName(fClassName);
        beanFindParam.setFieldName(psiField.getName());
        beanFindParam.setBeanName(ReflectStringUtils.beanName(fClassName));
        return beanFindParam;
    }
}
