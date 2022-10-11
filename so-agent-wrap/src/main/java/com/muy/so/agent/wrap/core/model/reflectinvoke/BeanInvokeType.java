package com.muy.so.agent.wrap.core.model.reflectinvoke;

import lombok.Getter;

/**
 * @Author jiyanghuang
 * @Date 2022/5/26 01:10
 */
public enum BeanInvokeType {

    ONLY_METHOD(1, "bean仅调用方法"),
    CONSTRUCT_INVOKE_METHOD(2, "先构建方法再调用"),
    STATIC_METHOD(3, "静态方法");;

    @Getter
    private int code;
    @Getter
    private String desc;

    BeanInvokeType(int code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
