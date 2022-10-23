package com.muy.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @Author jiyanghuang
 * @Date 2022/6/23 00:25
 */
public class JsonLogUtils {

    public static List<String> jsons(String jsonLine) {
        String logLine = jsonLine;
        if (StringUtils.isBlank(logLine)) {
            logLine = ClipboardUtils.fetchStringFromClip();
        }
        char[] charr = logLine.toCharArray();
        int len = charr.length;
        List<String> jsons = Lists.newArrayList();
        StringBuilder jsonSb = new StringBuilder();
        int braceCount = 0;
        for (int i = 0; i < len; i++) {
            if ('{' == charr[i]) {
                braceCount++;
                jsonSb.append(charr[i]);
            } else if ('}' == charr[i]) {
                braceCount--;
                jsonSb.append(charr[i]);
                if (0 == braceCount) {
                    jsons.add(StringEscapeUtils.unescapeJson(jsonSb.toString()));
                    jsonSb = new StringBuilder();
                }
            } else {
                if (braceCount > 0) {
                    jsonSb.append(charr[i]);
                }
            }
        }
        return jsons;
    }
}
