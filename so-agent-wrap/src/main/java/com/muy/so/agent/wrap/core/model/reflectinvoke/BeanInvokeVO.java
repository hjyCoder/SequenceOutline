package com.muy.so.agent.wrap.core.model.reflectinvoke;

/**
 * @Author jiyanghuang
 * @Date 2022/10/10 21:09
 */
public class BeanInvokeVO {

    /**
     * 调用的全类名
     */
    private String classFullName;

    /**
     * bean 的名称
     */
    private String beanName;

    /**
     * 如果存在Context则按beanName来调用
     * 否则生成一个对象来调用
     */
    private int invokeType;

    /**
     * 构建函数描述
     */
    private MethodInvokeVO constructorMethod;

    /**
     * 调用的方法
     */
    private MethodInvokeVO method;

    /**
     * 表示如何获取Bean
     * 间接获取的方式
     */
    private BeanFindVO beanFind;

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

    public int getInvokeType() {
        return invokeType;
    }

    public void setInvokeType(int invokeType) {
        this.invokeType = invokeType;
    }

    public MethodInvokeVO getConstructorMethod() {
        return constructorMethod;
    }

    public void setConstructorMethod(MethodInvokeVO constructorMethod) {
        this.constructorMethod = constructorMethod;
    }

    public MethodInvokeVO getMethod() {
        return method;
    }

    public void setMethod(MethodInvokeVO method) {
        this.method = method;
    }

    public BeanFindVO getBeanFind() {
        return beanFind;
    }

    public void setBeanFind(BeanFindVO beanFind) {
        this.beanFind = beanFind;
    }
}
