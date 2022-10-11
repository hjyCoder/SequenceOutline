package com.muy.so.agent.wrap.core.model.reflectinvoke;

import com.muy.so.agent.wrap.core.util.ReflectInvokeUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author jiyanghuang
 * @Date 2022/10/10 21:04
 */
public class MethodInvokeVO {

    /**
     * 构建函数名称
     */
    public static final String CONSTRUCT_METHOD_NAME = "<init>";

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 方法参数全类名，调用方法
     * javaTypeCanonical
     */
    private List<String> mpjtcs;

    /**
     * 参数值
     * 注意所有的值都是按List来传的，从而避开基本类型
     */
    private List<List<Object>> mpjtcsValueJson;

    public static String getConstructMethodName() {
        return CONSTRUCT_METHOD_NAME;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getMpjtcs() {
        return mpjtcs;
    }

    public void setMpjtcs(List<String> mpjtcs) {
        this.mpjtcs = mpjtcs;
    }

    public List<List<Object>> getMpjtcsValueJson() {
        return mpjtcsValueJson;
    }

    public void setMpjtcsValueJson(List<List<Object>> mpjtcsValueJson) {
        this.mpjtcsValueJson = mpjtcsValueJson;
    }
}
