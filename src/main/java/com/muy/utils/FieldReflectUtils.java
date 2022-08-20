package com.muy.utils;

import com.intellij.jsonpath.ui.JsonPathEvaluateManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.ui.AnimatedIcon;

import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * @Author jiyanghuang
 * @Date 2022/8/20 04:36
 */
public class FieldReflectUtils {

    public static final String JSON_PATH_EVALUATE_EXPRESSION_KEY = "JSON_PATH_EVALUATE_EXPRESSION_KEY";

    public static final String JSON_PATH_EVALUATE_SOURCE_KEY = "JSON_PATH_EVALUATE_SOURCE_KEY";

    public static final String JSON_PATH_EVALUATE_RESULT_KEY = "JSON_PATH_EVALUATE_RESULT_KEY";

    public static final String ANIMATION_IN_RENDERER_ALLOWED = "ANIMATION_IN_RENDERER_ALLOWED";

    public static void findKey(Project project, String key, Consumer<Key> consumer) {
        try {
            JsonPathEvaluateManager jsonPathEvaluateManager = JsonPathEvaluateManager.getInstance(project);
            Field[] fields = jsonPathEvaluateManager.getClass().getFields();
            for (Field field : fields) {
                if (key.equals(field.getName())) {
                    field.setAccessible(true);
                    consumer.accept((Key) field.get(jsonPathEvaluateManager));
                    break;
                }
            }
        } catch (Exception ex) {

        }
    }

    public static void treeLoading(Consumer<Key> consumer) {
        try {
            Field[] fields = AnimatedIcon.class.getFields();
            for (Field field : fields) {
                if (ANIMATION_IN_RENDERER_ALLOWED.equals(field.getName())) {
                    field.setAccessible(true);
                    consumer.accept((Key) field.get(null));
                    break;
                }
            }
        } catch (Exception ex) {

        }
    }
}
