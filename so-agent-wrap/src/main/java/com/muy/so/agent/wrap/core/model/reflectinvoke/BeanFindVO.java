package com.muy.so.agent.wrap.core.model.reflectinvoke;

/**
 * @Author jiyanghuang
 * @Date 2022/10/12 19:47
 */
public class BeanFindVO {
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

    public String getClassFullName() {
        return classFullName;
    }

    public void setClassFullName(String classFullName) {
        this.classFullName = classFullName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
