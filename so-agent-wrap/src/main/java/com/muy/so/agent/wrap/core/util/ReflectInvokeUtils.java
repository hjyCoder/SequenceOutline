package com.muy.so.agent.wrap.core.util;

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
                result.add(values.get(0));
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
            if (Object.class.equals(clazz.getSuperclass())) {
                return null;
            }
            Method parentFind = findMethod(clazz.getSuperclass(), methodName, classes);
            if (null != parentFind) {
                return parentFind;
            }
            for (Class<?> inf : clazz.getInterfaces()) {
                if (Object.class.equals(inf)) {
                    return null;
                }
                Method infFind = findMethod(clazz.getSuperclass(), methodName, classes);
                if (null != infFind) {
                    return infFind;
                }
            }
            return null;
        }
    }


    public static <T> String ofJson(T data) {
        AgentResult agentResult = AgentResult.of(data);
        return JacksonUtils.toJSONString(agentResult);
    }

    public static <T> String ofFailJson(String message) {
        AgentResult agentResult = AgentResult.ofFail(message);
        return JacksonUtils.toJSONString(agentResult);
    }
}
