package com.muy.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Supplier;

/**
 * @Author jiyanghuang
 * @Date 2022/4/6 4:10 PM
 */
public class JacksonUtils {

    private static final Logger LOGGER = Logger.getInstance(JacksonUtils.class);

    private static ObjectMapper mapper;

    /**
     * 用于特殊场景
     * 比如工具需要显示不为空的属性等场景
     */
    private static ObjectMapper mapperWithNull;

    private static final Map<String, Pair<Class<?>, String>> baseReferenceTransforms = new HashMap();


    /**
     * 设置一些通用的属性
     */
    static {
        mapper = new ObjectMapper();
        // 如果json中有新增的字段并且是实体类类中不存在的，不报错
        // mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        // 如果存在未知属性，则忽略不报错
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 这个属性最好为true,方便及时发现问题
//        mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);

        // 允许key没有双引号
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许key有单引号
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 允许整数以0开头
        mapper.configure(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature(), true);
        // 允许字符串中存在回车换行控制符
        mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);

        // 排序需要的关键发属性配置
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

        /**
         * 只输出不为空的属性值
         */
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

//        /**
//         * 有了该配置后，生成的时间可能前端是无法解析的，因为它是一个数组，而不是一个字符串或者long
//         */
//        mapper.registerModule(new JavaTimeModule());

        baseReferenceTransforms.put("byte", Pair.of(Byte.TYPE, "java.lang.Byte"));
        baseReferenceTransforms.put("char", Pair.of(Character.TYPE, "java.lang.Character"));
        baseReferenceTransforms.put("double", Pair.of(Double.TYPE, "java.lang.Double"));
        baseReferenceTransforms.put("float", Pair.of(Float.TYPE, "java.lang.Float"));
        baseReferenceTransforms.put("int", Pair.of(Integer.TYPE, "java.lang.Integer"));
        baseReferenceTransforms.put("long", Pair.of(Long.TYPE, "java.lang.Long"));
        baseReferenceTransforms.put("short", Pair.of(Short.TYPE, "java.lang.Short"));
        baseReferenceTransforms.put("boolean", Pair.of(Boolean.TYPE, "java.lang.Boolean"));

        baseReferenceTransforms.put("B", Pair.of(Byte.TYPE, "java.lang.Byte"));
        baseReferenceTransforms.put("C", Pair.of(Character.TYPE, "java.lang.Character"));
        baseReferenceTransforms.put("D", Pair.of(Double.TYPE, "java.lang.Double"));
        baseReferenceTransforms.put("F", Pair.of(Float.TYPE, "java.lang.Float"));
        baseReferenceTransforms.put("I", Pair.of(Integer.TYPE, "java.lang.Integer"));
        baseReferenceTransforms.put("J", Pair.of(Long.TYPE, "java.lang.Long"));
        baseReferenceTransforms.put("S", Pair.of(Short.TYPE, "java.lang.Short"));
        baseReferenceTransforms.put("Z", Pair.of(Boolean.TYPE, "java.lang.Boolean"));


        mapperWithNull = new ObjectMapper();
        // 如果json中有新增的字段并且是实体类类中不存在的，不报错
        // mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        // 如果存在未知属性，则忽略不报错
        mapperWithNull.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 这个属性最好为true,方便及时发现问题
//        mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);

        // 允许key没有双引号
        mapperWithNull.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许key有单引号
        mapperWithNull.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 允许整数以0开头
        mapperWithNull.configure(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature(), true);
        // 允许字符串中存在回车换行控制符
        mapperWithNull.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);

        // 排序需要的关键发属性配置
        mapperWithNull.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }

    /**
     * 转换成Json
     *
     * @param obj
     * @return
     */
    public static String toJSONString(Object obj) {
        return obj != null ? toJSONString(obj, () -> "", false) : "";
    }

    /**
     * 转换成Json
     *
     * @param obj
     * @return
     */
    public static String toJSONStringWithNull(Object obj) {
        return obj != null ? toJSONStringWithNull(obj, () -> "", false) : "";
    }

    /**
     * 主要解决当 List中的对象序列化，如果需要输出 @class 信息时，需要指明泛型，否则无法生成Json信息
     *
     * @param obj
     * @param typeReference
     * @param format
     * @return
     */
    public static String toJSONString(Object obj, TypeReference typeReference, boolean format) {
        try {
            if (obj == null) {
                return "";
            }
            if (obj instanceof String) {
                return obj.toString();
            }
            if (obj instanceof Number) {
                return obj.toString();
            }
            if (null != typeReference) {
                if (format) {
                    return mapper.writerFor(typeReference).withDefaultPrettyPrinter().writeValueAsString(obj);
                }
                return mapper.writerFor(typeReference).writeValueAsString(obj);
            }
            if (format) {
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            }
            return mapper.writeValueAsString(obj);
        } catch (Throwable e) {
            LOGGER.error(String.format("toJSONString %s", obj != null ? obj.toString() : "null"), e);
        }
        return "";
    }

    /**
     * 格式化Json
     *
     * @param obj
     * @return
     */
    public static String toFormatJSONString(Object obj) {
        return obj != null ? toJSONString(obj, () -> "", true) : "";
    }

    /**
     * @param obj
     * @param defaultSupplier
     * @param format
     * @return
     */
    public static String toJSONString(Object obj, Supplier<String> defaultSupplier, boolean format) {
        try {
            if (obj == null) {
                return defaultSupplier.get();
            }
            if (obj instanceof String) {
                return obj.toString();
            }
            if (obj instanceof Number) {
                return obj.toString();
            }
            if (format) {
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            }
            return mapper.writeValueAsString(obj);
        } catch (Throwable e) {
            LOGGER.error(String.format("toJSONString %s", obj != null ? obj.toString() : "null"), e);
        }
        return defaultSupplier.get();
    }

    /**
     * @param obj
     * @param defaultSupplier
     * @param format
     * @return
     */
    public static String toJSONStringWithNull(Object obj, Supplier<String> defaultSupplier, boolean format) {
        try {
            if (obj == null) {
                return defaultSupplier.get();
            }
            if (obj instanceof String) {
                return obj.toString();
            }
            if (obj instanceof Number) {
                return obj.toString();
            }
            if (format) {
                return mapperWithNull.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            }
            return mapperWithNull.writeValueAsString(obj);
        } catch (Throwable e) {
            LOGGER.error(String.format("toJSONString %s", obj != null ? obj.toString() : "null"), e);
        }
        return defaultSupplier.get();
    }

    /**
     * @param obj
     * @param defaultSupplier
     * @param format
     * @return
     */
    public static String toJSONStringWithNull(Object obj, boolean format) {
        return toJSONStringWithNull(obj, () -> "{}", format);
    }

    /**
     * @param value
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T toJavaObject(String value, Class<T> tClass) {
        return StringUtils.isNotBlank(value) ? toJavaObject(value, tClass, () -> null) : null;
    }

    /**
     * @param obj
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T toJavaObject(Object obj, Class<T> tClass) {
        return obj != null ? toJavaObject(toJSONString(obj), tClass, () -> null) : null;
    }

    /**
     * @param value
     * @param tClass
     * @param defaultSupplier
     * @param <T>
     * @return
     */
    public static <T> T toJavaObject(String value, Class<T> tClass, Supplier<T> defaultSupplier) {
        try {
            if (StringUtils.isBlank(value)) {
                return defaultSupplier.get();
            }
            return mapper.readValue(value, tClass);
        } catch (Throwable e) {
            LOGGER.error(String.format("toJavaObject value = %s, tClass = %s", value, tClass), e);
        }
        return defaultSupplier.get();
    }

    /**
     * @param value
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> List<T> toJavaObjectList(String value, Class<T> tClass) {
        return StringUtils.isNotBlank(value) ? toJavaObjectList(value, tClass, () -> null) : null;
    }

    /**
     * @param obj
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> List<T> toJavaObjectList(Object obj, Class<T> tClass) {
        return obj != null ? toJavaObjectList(toJSONString(obj), tClass, () -> null) : null;
    }

    /**
     * @param value
     * @param tClass
     * @param defaultSupplier
     * @param <T>
     * @return
     */
    public static <T> List<T> toJavaObjectList(String value, Class<T> tClass, Supplier<List<T>> defaultSupplier) {
        try {
            if (StringUtils.isBlank(value)) {
                return defaultSupplier.get();
            }
            JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, tClass);
            return mapper.readValue(value, javaType);
        } catch (Throwable e) {
            LOGGER.error(String.format("toJavaObjectList exception \n%s\n%s", value, tClass), e);
        }
        return defaultSupplier.get();
    }

    /**
     * TypeReference typeReference2 = new TypeReference<List<T>>() {};
     * 直接按泛型来转换
     *
     * @param value
     * @param typeReference   在知道 class 类型的场景下使用
     * @param defaultSupplier
     * @param <T>
     * @return
     */
    public static <T> List<T> toJavaObjectListTypeRefer(String value, TypeReference<List<T>> typeReference, Supplier<List<T>> defaultSupplier) {
        try {
            if (StringUtils.isBlank(value)) {
                return defaultSupplier.get();
            }
            return mapper.readValue(value, typeReference);
        } catch (Throwable e) {
            LOGGER.error(String.format("toJavaObjectListTypeRefer value = %s, typeReference = %s", value, typeReference), e);
        }
        return defaultSupplier.get();
    }

    /**
     * @param value           json 串
     * @param javaType        需要转换的java类型
     * @param defaultSupplier
     * @param <T>
     * @return
     */
    public static <T> List<T> toJavaObjectListJavaType(String value, JavaType javaType, Supplier<List<T>> defaultSupplier) {
        try {
            if (StringUtils.isBlank(value)) {
                return defaultSupplier.get();
            }
            return mapper.readValue(value, javaType);
        } catch (Throwable e) {
            LOGGER.error(String.format("toJavaObjectListJavaType value = %s, javaType = %s", value, javaType), e);
            return defaultSupplier.get();
        }
    }


    /**
     * @param value             json 串
     * @param javaTypeCanonical List<T> 中T的全类名
     * @param defaultSupplier   默认值
     * @param <T>
     * @return
     */
    public static <T> List<T> toJavaObjectList(String value, String javaTypeCanonical, Supplier<List<T>> defaultSupplier) {
        try {
            if (StringUtils.isBlank(value)) {
                return defaultSupplier.get();
            }
            Pair<Class<?>, String> replace = baseReferenceTransforms.get(javaTypeCanonical);
            if (null != replace) {
                javaTypeCanonical = replace.getRight();
            }
            return mapper.readValue(value, javaType(appendList(javaTypeCanonical)));
        } catch (Throwable e) {
            LOGGER.error(String.format("toJavaObjectList value = %s, javaTypeCanonical = %s", value, javaTypeCanonical), e);
        }
        return defaultSupplier.get();
    }

    /**
     * @param value             json 串
     * @param javaTypeCanonical List<T> 中T的全类名
     * @param defaultSupplier   默认值
     * @param <T>
     * @return
     */
    public static <T> List<T> toJavaObjectList(String value, String javaTypeCanonical) {
        try {
            Pair<Class<?>, String> replace = baseReferenceTransforms.get(javaTypeCanonical);
            if (null != replace) {
                javaTypeCanonical = replace.getRight();
            }
            return mapper.readValue(value, javaType(appendList(javaTypeCanonical)));
        } catch (Throwable e) {
            LOGGER.error(String.format("toJavaObjectList value = %s, javaTypeCanonical = %s", value, javaTypeCanonical), e);
            return Lists.newArrayList();
        }
    }

    // 简单地直接用json复制或者转换(Cloneable)
    public static <T> T jsonCopy(Object obj, Class<T> tClass) {
        return obj != null ? toJavaObject(toJSONString(obj), tClass) : null;
    }

    /**
     * @param value
     * @return
     */
    public static Map<String, Object> toMap(String value) {
        return StringUtils.isNotBlank(value) ? toMap(value, () -> null) : null;
    }

    /**
     * @param value
     * @return
     */
    public static Map<String, Object> toMap(Object value) {
        return value != null ? toMap(value, () -> null) : null;
    }

    /**
     * @param value
     * @param defaultSupplier
     * @return
     */
    public static Map<String, Object> toMap(Object value, Supplier<Map<String, Object>> defaultSupplier) {
        if (value == null) {
            return defaultSupplier.get();
        }
        try {
            if (value instanceof Map) {
                return (Map<String, Object>) value;
            }
        } catch (Exception e) {
            LOGGER.error(String.format("toMap value = %s", value), e);
        }
        return toMap(toJSONString(value), defaultSupplier);
    }

    /**
     * @param value
     * @param defaultSupplier
     * @return
     */
    public static Map<String, Object> toMap(String value, Supplier<Map<String, Object>> defaultSupplier) {
        if (StringUtils.isBlank(value)) {
            return defaultSupplier.get();
        }
        try {
            return toJavaObject(value, LinkedHashMap.class);
        } catch (Exception e) {
            LOGGER.error(String.format("toMap value = %s", value), e);
        }
        return defaultSupplier.get();
    }

    /**
     * @param value
     * @return
     */
    public static List<Object> toList(String value) {
        return StringUtils.isNotBlank(value) ? toList(value, () -> null) : null;
    }

    /**
     * @param value
     * @return
     */
    public static List<Object> toList(Object value) {
        return value != null ? toList(value, () -> null) : null;
    }

    /**
     * @param value
     * @param defaultSuppler
     * @return
     */
    public static List<Object> toList(String value, Supplier<List<Object>> defaultSuppler) {
        if (StringUtils.isBlank(value)) {
            return defaultSuppler.get();
        }
        try {
            return toJavaObject(value, List.class);
        } catch (Exception e) {
            LOGGER.error(String.format("toList value = %s", value), e);
        }
        return defaultSuppler.get();
    }

    /**
     * 转换成 List
     *
     * @param value
     * @param defaultSuppler 获取默认的函数
     * @return
     */
    public static List<Object> toList(Object value, Supplier<List<Object>> defaultSuppler) {
        if (value == null) {
            return defaultSuppler.get();
        }
        if (value instanceof List) {
            return (List<Object>) value;
        }
        return toList(toJSONString(value), defaultSuppler);
    }

    /**
     * 获取 long 类型
     *
     * @param map
     * @param key
     * @return
     */
    public static long getLong(Map<String, Object> map, String key) {
        if (MapUtils.isEmpty(map)) {
            return 0L;
        }
        String valueStr = String.valueOf(map.get(key));
        if (StringUtils.isBlank(valueStr) || !StringUtils.isNumeric(valueStr)) {
            return 0L;
        }
        return Long.valueOf(valueStr);
    }

    /**
     * 根据 key 获取整数
     *
     * @param map
     * @param key
     * @return
     */
    public static int getInt(Map<String, Object> map, String key) {
        if (MapUtils.isEmpty(map)) {
            return 0;
        }
        String valueStr = String.valueOf(map.get(key));
        if (StringUtils.isBlank(valueStr) || !StringUtils.isNumeric(valueStr)) {
            return 0;
        }
        return Integer.valueOf(valueStr);
    }

    /**
     * 对 json 进行排序
     *
     * @param json
     * @return
     */
    public static String sortJson(String json) {
        try {
            // 起作用的是属性配置
            Map map = mapper.readValue(json, TreeMap.class);
            return mapper.writeValueAsString(map);

        } catch (Exception ex) {
            return json;
        }
    }

    /**
     * json 串转换成 jsonNode 节点
     * 转换成模型结构来遍历 json 串
     *
     * @param jsonStr
     * @return
     * @throws Exception
     */
    public static JsonNode toJsonNode(String jsonStr) throws Exception {
        JsonNode jsonNode = mapper.readTree(jsonStr);
        return jsonNode;
    }

    /**
     * json 串转换成 jsonNode 节点
     * 转换成模型结构来遍历 json 串
     *
     * @param jsonStr
     * @return
     * @throws Exception
     */
    public static JsonNode toJsonNodeIgnoreEX(String jsonStr) {
        try {
            JsonNode jsonNode = mapper.readTree(jsonStr);
            return jsonNode;
        } catch (Exception ex) {
            LOGGER.error(String.format("toJsonNodeIgnoreEX jsonStr = %s", jsonStr), ex);
            return null;
        }
    }

    /**
     * TypeReference 生成 全类名 + 泛类型
     * 根据类型声明来取全类名
     *
     * @param typeReference
     * @return
     */
    public static String javaTypeCanonical(TypeReference typeReference) {
        return mapper.getTypeFactory().constructType(typeReference).toCanonical();
    }

    /**
     * 在单个全类名 + 泛型的基础上添加成 List
     * 方便形成List<Object>的形式
     *
     * @param javaTypeCanonical
     * @return
     */
    public static String appendList(String javaTypeCanonical) {
        StringBuilder sb = new StringBuilder();
        sb.append("java.util.List<");
        sb.append(javaTypeCanonical);
        sb.append(">");
        return sb.toString();
    }

    /**
     * 根据全类名 + 泛型数据生成 JavaType
     * 可以配合IDE插件生成全类名进行使用
     *
     * @param javaTypeCanonical
     * @return
     */
    public static JavaType javaType(String javaTypeCanonical) {
        return mapper.getTypeFactory().constructFromCanonical(javaTypeCanonical);
    }

    /**
     * 根据全类名 + 泛型数据生成 JavaType
     * 可以配合IDE插件生成全类名进行使用
     *
     * @param javaTypeCanonical
     * @return
     */
    public static List<Class<?>> javaTypes(List<String> javaTypeCanonicals) {
        if (CollectionUtils.isEmpty(javaTypeCanonicals)) {
            return Lists.newArrayList();
        }
        List<Class<?>> result = Lists.newArrayListWithExpectedSize(javaTypeCanonicals.size());
        for (String javaTypeCanonical : javaTypeCanonicals) {
            Pair<Class<?>, String> replace = baseReferenceTransforms.get(javaTypeCanonical);
            if (null != replace) {
                result.add(replace.getLeft());
                continue;
            }
            JavaType javaType = mapper.getTypeFactory().constructFromCanonical(javaTypeCanonical);
            result.add(javaType.getRawClass());
        }
        return result;
    }

    /**
     * 转换形如 ["fullClassName":[]]
     *
     * @param jsonStr
     * @return
     */
    public static Map<String, List<Object>> convertJsonPairList(String jsonStr) {
        return convertJsonPairList(jsonStr, null);
    }

    /**
     * 转换形如 ["fullClassName":[]]
     *
     * @param jsonStr
     * @return
     */
    public static Map<String, List<Object>> convertJsonPairList(String jsonStr, Map<String, List<Object>> appendMap) {
        try {
            Map<String, List<Object>> re = null == appendMap ? Maps.newHashMap() : appendMap;
            if (StringUtils.isBlank(jsonStr)) {
                return re;
            }
            JsonNode jsonNode = toJsonNode(jsonStr);
            if (!JsonNodeType.OBJECT.equals(jsonNode.getNodeType())) {
                return re;
            }
            ObjectNode objectNode = (ObjectNode) jsonNode;
            objectNode.fields().forEachRemaining(entry -> {
                re.put(entry.getKey(), toJavaObjectList(entry.getValue().toString(), entry.getKey()));
            });
            return null;
        } catch (Exception ex) {
            LOGGER.error(String.format("convertJsonPairList jsonStr = %s, appendMap = %s", jsonStr, appendMap), ex);
            return Maps.newHashMap();
        }
    }


    /**
     * 对 json 进行排序,包括List
     *
     * @param json
     * @return
     */
    public static String sortJsonIncludeList(String json) {
        try {
            // 起作用的是属性配置
            if (json.startsWith("[")) {
                return sortJsonList(json);
            }
            Map map = mapper.readValue(json, TreeMap.class);
            sortMapValue(map);
            return mapper.writeValueAsString(map);
        } catch (Exception ex) {
            ex.printStackTrace();
            return json;
        }
    }

    private static String sortJsonList(String json) {
        try {
            List<TreeMap> map = mapper.readValue(json, new TypeReference<List<TreeMap>>() {
            });
            sortListValueMap((List) map);
            return mapper.writeValueAsString(map);
        } catch (Exception ex) {
            // 如果异常说明是简单的类型
            try {
                List<Object> objectList = toList(json);
                primitiveSort(objectList);
                return mapper.writeValueAsString(objectList);
            } catch (Exception exc) {
                ex.printStackTrace();
                return json;
            }
        }
    }

    private static void sortMapValue(Map map) {
        if (null == map) {
            return;
        }
        map.forEach((k, v) -> {
            if (v instanceof List) {
                List listT = (List) v;
                if (CollectionUtils.isNotEmpty(listT)) {
                    Object obj = listT.get(0);
                    if (obj.getClass().isPrimitive()) {
                        primitiveSort(listT);
                    } else if (obj instanceof Map) {
                        sortListValueMap(listT);
                    }
                }
            } else if (v instanceof Map) {
                sortMapValue((Map) v);
            }
        });
    }

    /**
     * 后续遍历的方式
     *
     * @param listMap
     */
    private static void sortListValueMap(List<Map> listMap) {
        for (Object cObj : listMap) {
            sortMapValue((Map) cObj);
        }
        Map<Object, String> localCache = Maps.newHashMap();
        listMap.sort(new Comparator<Map>() {
            @Override
            public int compare(Map o1, Map o2) {
                String jsono1 = localCache.computeIfAbsent(o1, (o) -> {
                    return JacksonUtils.toJSONString(o);
                });
                String jsono2 = localCache.computeIfAbsent(o2, (o) -> {
                    return JacksonUtils.toJSONString(o);
                });
                return jsono1.compareTo(jsono2);
            }
        });
    }

    private static void primitiveSort(List listT) {
        Object obj = listT.get(0);
        if (obj instanceof Comparable) {
            listT.sort(new Comparator<Comparable>() {
                @Override
                public int compare(Comparable o1, Comparable o2) {
                    return o1.compareTo(o2);
                }
            });
        } else {
            listT.stream().sorted();
        }
    }
}
