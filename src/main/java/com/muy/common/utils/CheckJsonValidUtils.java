package com.muy.common.utils;

import com.muy.common.exception.ResponseCodeEnum;
import com.muy.common.exception.SequenceOutlineException;

import java.util.function.Predicate;

/**
 * @Author jiyanghuang
 * @Date 2022/5/8 6:38 PM
 */
public class CheckJsonValidUtils {

    public static <T> T toJavaObject(String jsonStr, Class<T> tClass, Predicate<T> predicate) {
        T t = JacksonUtils.toJavaObject(jsonStr, tClass);
        if (null == t) {
            throw new SequenceOutlineException(ResponseCodeEnum.JSON_STR_INVALID);
        }
        if (null != predicate) {
            if (!predicate.test(t)) {
                throw new SequenceOutlineException(ResponseCodeEnum.JSON_STR_INVALID_INVALID_OBJ);
            }
        }
        return t;
    }
}
