package com.muy.common.exception;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public enum ResponseCodeEnum {
    SUCCESS("200", "操作成功"),

    PARAM_INVALID("400", "参数非法"),
    JSON_STR_INVALID("5010101", "json字符串异常"),
    JSON_STR_INVALID_INVALID_OBJ("5010102", "json无法转换成对应的对象"),
    ;

    private String code;
    private String desc;

    ResponseCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
