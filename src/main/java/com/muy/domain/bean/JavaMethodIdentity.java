package com.muy.domain.bean;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class JavaMethodIdentity extends Identity{


    /**
     * 获取触发调用事件的类名称
     */
    public final String javaClassName;

    /**
     * 获取触发调用事件的方法名称
     */
    public final String javaMethodName;

    /**
     * 获取触发调用事件的方法签名
     */
    public final String javaMethodDesc;

    public JavaMethodIdentity(String javaClassName, String javaMethodName, String javaMethodDesc){
        super("java", javaClassName, javaMethodName + "~" + javaMethodDesc, null);
        this.javaClassName = javaClassName;
        this.javaMethodName = javaMethodName;
        this.javaMethodDesc = javaMethodDesc;
    }

}
