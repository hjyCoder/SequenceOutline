package com.muy.common.bean;

import com.muy.utils.JacksonUtils;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**
 * @Author jiyanghuang
 * @Date 2022/5/26 01:00
 */
@Data
public class BeanInvokeMethodDesc {

    /**
     * 构造函数名称
     */
    public static final String CONSTRUCTOR_METHOD_NAME = "<init>";

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 方法参数全类名，调用方法
     * javaTypeCanonical
     */
    private List<String> mpjtcs;

    /**
     * 参数值
     * 注意所有的值都是按List来传的，从而避开基本类型的转换
     */
    private List<List<Object>> mpjtcsValueJson;

    /**
     * 为了方便找到方法
     *
     * @return
     */
    public List<Class<?>> classes() {
        return JacksonUtils.javaTypes(mpjtcs);
    }

    public Class[] classesArr() {
        return classes().toArray(new Class[0]);
    }

//    public List<Object> paramValue() {
//        List<Object> result = Lists.newArrayList();
//        if (CollectionUtils.isNotEmpty(mpjtcsValueJson)) {
//            int len = mpjtcsValueJson.size();
//            for (int i = 0; i < len; i++) {
//                String jsonValues = mpjtcsValueJson.get(i);
//                String javaTypeCanonical = mpjtcs.get(i);
//                List<Object> values = JacksonUtils.toJavaObjectList(jsonValues, javaTypeCanonical);
//                result.add(values.get(0));
//            }
//        }
//        return result;
//    }

    /**
     * 默认构造方法
     *
     * @return
     */
    public static BeanInvokeMethodDesc defaultConstructor() {
        BeanInvokeMethodDesc beanInvokeMethodDesc = new BeanInvokeMethodDesc();
        beanInvokeMethodDesc.setMethodName(BeanInvokeMethodDesc.CONSTRUCTOR_METHOD_NAME);
        beanInvokeMethodDesc.setMpjtcs(Lists.newArrayList());
        beanInvokeMethodDesc.setMpjtcsValueJson(Lists.newArrayList());
        return beanInvokeMethodDesc;
    }
}
