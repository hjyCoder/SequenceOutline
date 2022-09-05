package com.muy.utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author jiyanghuang
 * @Date 2022/5/22 11:41
 */
public class JsonCommonUtils {
    public static boolean isJson(String str) {
        return isJsonObj(str) || isJsonArray(str);
    }

    /**
     * 是否为JSONObject字符串，首尾都为大括号判定为JSONObject字符串
     *
     * @param str 字符串
     * @return 是否为JSON字符串
     * @since 3.3.0
     */
    public static boolean isJsonObj(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        return isWrap(str.trim(), '{', '}');
    }

    /**
     * 是否为JSONArray字符串，首尾都为中括号判定为JSONArray字符串
     *
     * @param str 字符串
     * @return 是否为JSON字符串
     * @since 3.3.0
     */
    public static boolean isJsonArray(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        return isWrap(str.trim(), '[', ']');
    }

    public static boolean isWrap(CharSequence str, String prefix, String suffix) {
        if(StringUtils.isBlank(str)){
            return false;
        }
        final String str2 = str.toString();
        return str2.startsWith(prefix) && str2.endsWith(suffix);
    }

    /**
     * 指定字符串是否被同一字符包装（前后都有这些字符串）
     *
     * @param str     字符串
     * @param wrapper 包装字符串
     * @return 是否被包装
     */
    public static boolean isWrap(CharSequence str, String wrapper) {
        return isWrap(str, wrapper, wrapper);
    }

    /**
     * 指定字符串是否被同一字符包装（前后都有这些字符串）
     *
     * @param str     字符串
     * @param wrapper 包装字符
     * @return 是否被包装
     */
    public static boolean isWrap(CharSequence str, char wrapper) {
        return isWrap(str, wrapper, wrapper);
    }

    /**
     * 指定字符串是否被包装
     *
     * @param str        字符串
     * @param prefixChar 前缀
     * @param suffixChar 后缀
     * @return 是否被包装
     */
    public static boolean isWrap(CharSequence str, char prefixChar, char suffixChar) {
        if (null == str) {
            return false;
        }
        return str.charAt(0) == prefixChar && str.charAt(str.length() - 1) == suffixChar;
    }

    /**
     * 判断是否为转义后的Json串
     * @param text
     * @return
     */
    public static boolean isEscapeJson(String text) {
        if (isJson(text)) {
            return text.contains("\\\"");
        } else if (isJsonArray(text)) {
            return text.contains("\\\"");
        }
        return false;
    }

    public static String escapeJson(String jsonText){
        return StringEscapeUtils.escapeJson(jsonText);
    }

    public static String unescapeJson(String escapeJsonText){
        return StringEscapeUtils.unescapeJson(escapeJsonText);
    }
}
