package com.muy.view.window.rest.convert;

import com.google.common.collect.Maps;
import com.intellij.psi.PsiMethod;
import com.muy.constant.SequenceConstant;
import com.muy.utils.JacksonUtils;
import org.apache.commons.collections.MapUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class JsonConvert extends BaseConvert<Object> {

    public JsonConvert() {
    }

    public JsonConvert(@NotNull PsiMethod psiMethod) {
        super(psiMethod);
    }

    @Override
    public Map<String, String> formatString() {
        Map<String, String> jsonStrResult = Maps.newHashMap();
        Map<String, Map<String,Object>> methodParams = parseMethodParams();
        genJsonStr(jsonStrResult, methodParams, SequenceConstant.PARAM_QUERY_KEY);
        genJsonStr(jsonStrResult, methodParams, SequenceConstant.PARAM_BODY_KEY);
        return jsonStrResult;
    }

    private void genJsonStr(Map<String, String> jsonStrResult, Map<String, Map<String,Object>> methodParams, String key){
        Map<String,Object> temp = methodParams.get(key);
        if(null != temp && MapUtils.isNotEmpty(temp)){
            jsonStrResult.put(key, JacksonUtils.toFormatJSONString(temp));
        }
    }

    @Override
    public Map<String, Object> formatMap(@NotNull String paramsStr) {
//        return new JSONObject(paramsStr);
        return null;
    }
}

