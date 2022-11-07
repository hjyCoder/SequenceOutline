package com.muy.view.window.rest.convert;

import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.muy.constant.SequenceConstant;
import com.muy.view.window.rest.annotation.SpringHttpMethodAnnotation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseConvert<V> {

    private PsiMethod psiMethod;

    /**
     * 是否是基本数据类型
     */
    private boolean isBasicDataTypes;

    /**
     * 是基本数据类型时的参数名
     */
    private String basicDataParamName;

    public BaseConvert() {
    }

    public BaseConvert(@NotNull PsiMethod psiMethod) {
        this.setPsiMethod(psiMethod);
    }

    public void setPsiMethod(@NotNull PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    /**
     * 标注了@RequestBody注解则使用application/json格式
     */
    public boolean isRaw() {
        if (psiMethod == null) {
            return false;
        }

        for (PsiParameter parameter : psiMethod.getParameterList().getParameters()) {
            for (PsiAnnotation annotation : parameter.getAnnotations()) {
                // 参数标注的注解
                String qualifiedName = annotation.getQualifiedName();
                if (SpringHttpMethodAnnotation.REQUEST_BODY.getQualifiedName().equals(qualifiedName)) {
                    PsiType parameterType = parameter.getType();
                    this.isBasicDataTypes = isBasicDataTypes(parameterType.getCanonicalText());
                    if (this.isBasicDataTypes) {
                        basicDataParamName = parameter.getName();
                    }
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isBasicDataTypes(String type) {
        if (type == null) {
            return false;
        }
        final String[] classes = new String[]{
                String.class.getName(),
                Boolean.class.getName(),
                Byte.class.getName(),
                Character.class.getName(),
                Double.class.getName(),
                Float.class.getName(),
                Integer.class.getName(),
                Long.class.getName(),
                Short.class.getName(),
                "boolean",
                "byte",
                "char",
                "double",
                "float",
                "int",
                "long",
                "short",
        };
        for (String clazz : classes) {
            if (clazz.equals(type)) {
                return true;
            }
        }
        return false;
    }

    public boolean isBasicDataTypes() {
        return isBasicDataTypes;
    }

    public String getBasicDataParamName() {
        return basicDataParamName;
    }

    /**
     * parse method param
     *
     * @return map
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Map<String, V>> parseMethodParams() {
        if (psiMethod == null) {
            return Collections.emptyMap();
        }

        PsiParameterList parameterList = psiMethod.getParameterList();
        if (parameterList.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Map<String, V>> allMap = new HashMap<>();
        Map<String, V> queryParamMap = new HashMap<>();
        Map<String, V> bodyParamMap = new HashMap<>();
        allMap.put(SequenceConstant.PARAM_QUERY_KEY, queryParamMap);
        allMap.put(SequenceConstant.PARAM_BODY_KEY, bodyParamMap);
        for (PsiParameter parameter : parameterList.getParameters()) {
            PsiAnnotation[] parameterAnnotations = parameter.getAnnotations();
            String parameterName = parameter.getName();
            PsiType parameterType = parameter.getType();
            boolean addSuccess = false;
            for (PsiAnnotation parameterAnnotation : parameterAnnotations) {
                if (!(SpringHttpMethodAnnotation.REQUEST_PARAM.getQualifiedName().equals(parameterAnnotation.getQualifiedName())
                || SpringHttpMethodAnnotation.REQUEST_PATH_VARIABLE.getQualifiedName().equals(parameterAnnotation.getQualifiedName()))) {
                    continue;
                }
                List<JvmAnnotationAttribute> attributes = parameterAnnotation.getAttributes();
                for (JvmAnnotationAttribute attribute : attributes) {
                    String name = attribute.getAttributeName();
                    if (!("name".equals(name) || "value".equals(name))) {
                        continue;
                    }
//                    Object value = RestUtil.getAttributeValue(attribute.getAttributeValue());
//                    if (value != null) {
//                        parameterName = value.toString();
//                    }
                }
                if(SpringHttpMethodAnnotation.REQUEST_PATH_VARIABLE.getQualifiedName().equals(parameterAnnotation.getQualifiedName())){
                    parameterName = "{" + parameterName + "}";
                }
                putParam(queryParamMap, parameterName, parameterType);
                addSuccess = true;
            }
            // 区分 query_param 还是 body_param
            if(addSuccess){
                continue;
            }
            putParam(bodyParamMap, parameterName, parameterType);
        }
        return allMap;
    }

    private void putParam(Map<String, V> paramMap, String parameterName , PsiType parameterType){
        Object paramDefaultTypeValue = getTypeDefaultData(psiMethod, parameterType);
        if (paramDefaultTypeValue != null) {
            if (paramDefaultTypeValue instanceof Map) {
                //noinspection unchecked,rawtypes
                Map<String, V> value = (Map) paramDefaultTypeValue;
                value.forEach(paramMap::put);
            } else {
                paramMap.put(parameterName, (V) paramDefaultTypeValue);
            }
        }
    }

    @Nullable
    private Object getTypeDefaultData(@NotNull PsiMethod method, PsiType parameterType) {
        if (parameterType instanceof PsiArrayType) {
            return "[]";
        } else if (parameterType instanceof PsiClassReferenceType) {
            // Object | String | Integer | List<?> | Map<K, V>
            PsiClassReferenceType type = (PsiClassReferenceType) parameterType;

            GlobalSearchScope resolveScope = type.getResolveScope();
// 临时不需要该方法
//            PsiFile[] psiFiles = FilenameIndex.getFilesByName(
//                    method.getProject(),
//                    type.getName() + ".java",
//                    resolveScope
//            );
            PsiFile[] psiFiles = new PsiFile[0];
            if (psiFiles.length > 0) {
                for (PsiFile psiFile : psiFiles) {
                    if (psiFile instanceof PsiJavaFile) {
                        PsiClass[] fileClasses = ((PsiJavaFile) psiFile).getClasses();
                        Map<String, Object> map = new HashMap<>();
                        for (PsiClass psiClass : fileClasses) {
                            if (type.getReference().getQualifiedName().equals(psiClass.getQualifiedName())) {
                                PsiField[] fields = psiClass.getAllFields();
                                for (PsiField field : fields) {
                                    String fieldName = field.getName();
                                    Object defaultData = getTypeDefaultData(method, field.getType());
                                    map.put(fieldName, defaultData);
                                }
                                break;
                            }
                        }
                        return map;
                    }
                }
            } else {
                return getDefaultData(type.getName());
            }
        } else if (parameterType instanceof PsiPrimitiveType) {
            // int | char | boolean
            PsiPrimitiveType type = (PsiPrimitiveType) parameterType;
            return getDefaultData(type.getName());
        }
        return null;
    }

    @Contract(pure = true)
    private Object getDefaultData(@NotNull String classType) {
        Object data = null;
        switch (classType.toLowerCase()) {
            case "string":
                data = "demoData";
                break;
            case "char":
            case "character":
                data = 'A';
                break;
            case "byte":
            case "short":
            case "int":
            case "integer":
            case "long":
                data = 0;
                break;
            case "float":
            case "double":
                data = 0.0;
                break;
            case "boolean":
                data = true;
                break;
            default:
                break;
        }
        return data;
    }

    /**
     * get method'param to convert show String
     *
     * @return str
     */
    public abstract Map<String, String> formatString();

    /**
     * parse show String to convert Key-Value
     *
     * @param paramsStr show string
     * @return map
     */
    public abstract Map<String, V> formatMap(@NotNull String paramsStr);
}
