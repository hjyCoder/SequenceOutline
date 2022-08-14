package com.muy.common.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.application.WriteAction;
import com.intellij.util.ThrowableRunnable;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author jiyanghuang
 * @Date 2022/5/8 6:38 PM
 */
public class JsonUtils {

    private JsonUtils() {
    }

    public static String formatJson(String jsonStr) throws JsonProcessingException {
        Object jsonObject = Holder.MAPPER.readValue(jsonStr, Object.class);
        return Holder.MAPPER.writer(Holder.DEFAULT_PRETTY_PRINTER).writeValueAsString(jsonObject);
    }

    public static String minifyJson(String jsonStr) throws JsonProcessingException {
        Object jsonObject = Holder.MAPPER.readValue(jsonStr, Object.class);
        return Holder.MAPPER.writeValueAsString(jsonObject);
    }

    public static void verifyJson(String jsonStr) throws JsonProcessingException {
        Holder.MAPPER.readValue(jsonStr, Object.class);
    }

    public static String minifyJsonWrap(String jsonStr) {
        try {
            if (StringUtils.isBlank(jsonStr)) {
                return jsonStr;
            }
            if ((jsonStr.startsWith("{") && jsonStr.endsWith("}")) || (jsonStr.startsWith("[") && jsonStr.endsWith("]"))) {
                return minifyJson(jsonStr);
            }
            return jsonStr;
        } catch (Exception ex) {
            return jsonStr;
        }
    }

    public static String formatJsonWrap(String json) {
        if(StringUtils.isBlank(json)){
            return "";
        }
        try {
            WriteAction.run(new ThrowableRunnable<Throwable>() {
                @Override
                public void run() throws Throwable {
                    JsonUtils.verifyJson(json);
                }
            });
            return formatJson(json);
        } catch (JsonProcessingException jsonProcessingException) {
            String originalMessage = jsonProcessingException.getOriginalMessage();
            long charOffset = jsonProcessingException.getLocation().getCharOffset();
            return json;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return json;
        }
    }

    private static final class Holder {
        public static final ObjectMapper MAPPER = new ObjectMapper();
        public static final DefaultPrettyPrinter DEFAULT_PRETTY_PRINTER = new CustomPrettyPrinter();

        static {
            // 允许key没有双引号
            MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            // 允许整数以0开头
            MAPPER.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        }
    }

    private static final class CustomPrettyPrinter extends DefaultPrettyPrinter {

        private static final DefaultIndenter UNIX_LINE_FEED_INSTANCE = new DefaultIndenter("  ", "\n");

        public CustomPrettyPrinter() {
            super._objectFieldValueSeparatorWithSpaces = ": ";
            super._objectIndenter = UNIX_LINE_FEED_INSTANCE;
            super._arrayIndenter = UNIX_LINE_FEED_INSTANCE;
        }

        @Override
        public DefaultPrettyPrinter createInstance() {
            return new CustomPrettyPrinter();
        }
    }
}