package com.muy.utils;

import com.intellij.jsonpath.ui.JsonPathEvaluateManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.ui.AnimatedIcon;

import java.lang.reflect.Field;

/**
 * @Author jiyanghuang
 * @Date 2022/8/20 04:36
 */
public class FieldReflectUtils {

    public static final String JSON_PATH_EVALUATE_EXPRESSION_KEY = "JSON_PATH_EVALUATE_EXPRESSION_KEY";

    public static final String JSON_PATH_EVALUATE_SOURCE_KEY = "JSON_PATH_EVALUATE_SOURCE_KEY";

    public static final String JSON_PATH_EVALUATE_RESULT_KEY = "JSON_PATH_EVALUATE_RESULT_KEY";

    public static final String ANIMATION_IN_RENDERER_ALLOWED = "ANIMATION_IN_RENDERER_ALLOWED";

    public static Key findKey(Project project, String key) {
        try {
            JsonPathEvaluateManager jsonPathEvaluateManager = JsonPathEvaluateManager.getInstance(project);
            Field[] fields = jsonPathEvaluateManager.getClass().getFields();
            for (Field field : fields) {
                if (key.equals(field.getName())) {
                    field.setAccessible(true);
                    return (Key) field.get(jsonPathEvaluateManager);
                }
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Key treeLoading() {
        try {
            Field[] fields = AnimatedIcon.class.getFields();
            for (Field field : fields) {
                if (ANIMATION_IN_RENDERER_ALLOWED.equals(field.getName())) {
                    field.setAccessible(true);
                    return (Key) field.get(null);
                }
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}
