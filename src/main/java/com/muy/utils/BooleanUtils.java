package com.muy.utils;

/**
 * @Author jiyanghuang
 * @Date 2022/3/3 12:09 AM
 */
public class BooleanUtils {

    public static final int ZERO = 0;

    public static boolean zeroFalse(int value) {
        if (ZERO >= value) {
            return false;
        }
        return true;
    }

    public static boolean defaultFalse(Boolean value) {
        if (null == value) {
            return false;
        }
        return value;
    }

    public static boolean defaultTrue(Boolean value) {
        if (null == value) {
            return true;
        }
        return value;
    }
}
