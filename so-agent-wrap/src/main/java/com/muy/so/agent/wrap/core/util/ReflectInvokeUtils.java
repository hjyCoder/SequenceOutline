package com.muy.so.agent.wrap.core.util;

import com.fasterxml.jackson.databind.JavaType;
import com.google.common.collect.Lists;
import com.muy.so.agent.wrap.core.model.reflectinvoke.AgentResult;
import com.muy.so.agent.wrap.core.model.reflectinvoke.MethodInvokeVO;
import org.apache.commons.collections4.CollectionUtils;
import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author jiyanghuang
 * @Date 2022/10/10 21:19
 */
public class ReflectInvokeUtils {

    public static List<Class<?>> classes(MethodInvokeVO methodInvokeVO) {
        return JacksonUtils.javaTypes(methodInvokeVO.getMpjtcs());
    }

    public static Class[] classesArr(MethodInvokeVO methodInvokeVO) {
        return classes(methodInvokeVO).toArray(new Class[0]);
    }

    /**
     * @param methodInvokeVO
     * @return
     */
    public static List<Object> paramValue(MethodInvokeVO methodInvokeVO) {
        List<Object> result = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(methodInvokeVO.getMpjtcsValueJson())) {
            int len = methodInvokeVO.getMpjtcsValueJson().size();
            for (int i = 0; i < len; i++) {
                List<Object> jsonValues = methodInvokeVO.getMpjtcsValueJson().get(i);
                String javaTypeCanonical = methodInvokeVO.getMpjtcs().get(i);
                List<Object> values = JacksonUtils.toJavaObjectList(JacksonUtils.toJSONString(jsonValues), javaTypeCanonical);
                if (CollectionUtils.isNotEmpty(values)) {
                    result.add(values.get(0));
                } else {
                    JavaType javaType = JacksonUtils.javaType(javaTypeCanonical);
                    String objJson = JacksonUtils.toJSONString(jsonValues.get(0));
                    Object genObj = JacksonUtils.genObjParamConstructor(javaType.getRawClass());
                    if (null != genObj) {
                        JacksonUtils.jsonFillObj(objJson, genObj);
                    }
                    result.add(genObj);
                }
            }
        }
        return result;
    }

    public static Object[] paramArr(MethodInvokeVO methodInvokeVO) {
        return paramValue(methodInvokeVO).toArray(new Object[0]);
    }

    public static Method findMethod(Class<?> clazz, String methodName, Class[] classes) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, classes);
            return method;
        } catch (Exception ex) {
            if (null != clazz.getSuperclass() && !Object.class.equals(clazz.getSuperclass())) {
                Method parentFind = findMethod(clazz.getSuperclass(), methodName, classes);
                if (null != parentFind) {
                    return parentFind;
                }
            }

            for (Class<?> inf : clazz.getInterfaces()) {
                if (!Object.class.equals(inf)) {
                    Method infFind = findMethod(inf, methodName, classes);
                    if (null != infFind) {
                        return infFind;
                    }
                }
            }
            return null;
        }
    }

    /**
     *
     * @param fulClassName
     * @param clazz 该类对象是否实现了 fulClassName
     * @return
     */
    public static boolean inheritClassName(String fulClassName, Class<?> clazz) {
        if (fulClassName.equals(clazz.getName())) {
            return true;
        }
        if (null == clazz.getSuperclass()) {
            return false;
        }else if(inheritClassName(fulClassName, clazz.getSuperclass())){
            return true;
        }
        for (Class<?> inf : clazz.getInterfaces()) {
            if (inheritClassName(fulClassName, inf)) {
                return true;
            }
        }
        return false;
    }


    public static <T> String ofJson(T data) {
        AgentResult agentResult = AgentResult.of(data);
        return JacksonUtils.toJSONString(agentResult);
    }

    public static <T> String ofJson(T data, Object[] reqParam) {
        AgentResult agentResult = AgentResult.of(data);
        agentResult.setReqParam(reqParam);
        return JacksonUtils.toJSONString(agentResult);
    }

    public static <T> String ofFailJson(String message) {
        AgentResult agentResult = AgentResult.ofFail(message);
        return JacksonUtils.toJSONString(agentResult);
    }
}
