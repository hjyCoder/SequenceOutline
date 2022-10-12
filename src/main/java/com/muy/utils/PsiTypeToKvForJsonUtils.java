package com.muy.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.GsonBuilder;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.muy.common.exception.SequenceOutlineException;
import org.jetbrains.annotations.NonNls;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author jiyanghuang
 * @Date 2022/10/11 22:56
 */
public class PsiTypeToKvForJsonUtils {
    @NonNls
    private static final Map<String, Object> normalTypes = new HashMap<>();

    private static final GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();

    private static final BigDecimal zero = BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);

    static {

        LocalDateTime now = LocalDateTime.now();
        String dateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        normalTypes.put("Boolean", false);
        normalTypes.put("Float", zero);
        normalTypes.put("Double", zero);
        normalTypes.put("BigDecimal", zero);
        normalTypes.put("Number", 0);
        normalTypes.put("CharSequence", "");
        normalTypes.put("Date", dateTime);
        normalTypes.put("Temporal", now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        normalTypes.put("LocalDateTime", dateTime);
        normalTypes.put("LocalDate", now.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        normalTypes.put("LocalTime", now.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    public static List<Object> methodParamJson(PsiType psiType) {
        return Lists.newArrayList(psiTypeJsonObj(psiType));
    }

    public static Object psiTypeJsonObj(PsiType psiType) {
        Object dvObj = genDefault(psiType);
        if (null != dvObj) {
            return dvObj;
        }

        if (psiType instanceof PsiClassType) {
            PsiClassType psiClassType = (PsiClassType) psiType;
            return getFields(psiClassType.resolve());
        }

        if (psiType instanceof PsiArrayType) {
            PsiArrayType psiArrayType = (PsiArrayType) psiType;
            return Lists.newArrayList(psiTypeJsonObj(psiArrayType.getDeepComponentType()));
        }

        return new Object();
    }

    public static Map<String, Object> getFields(PsiClass psiClass) {
        Map<String, Object> map = new LinkedHashMap<>();

        if (psiClass == null) {
            return map;
        }

        for (PsiField field : psiClass.getAllFields()) {
            Set<String> fieldCircleSet = Sets.newHashSet();
            map.put(field.getName(), typeResolve(field.getType(), 0, fieldCircleSet));
        }

        return map;
    }


    private static Object typeResolve(PsiType type, int level, Set<String> fieldCircleSet) {

        level = ++level;

        if (type instanceof PsiPrimitiveType) {       //primitive Type

            return getDefaultValue(type);

        } else if (type instanceof PsiArrayType) {   //array type

            java.util.List<Object> list = new ArrayList<>();
            PsiType deepType = type.getDeepComponentType();
            list.add(typeResolve(deepType, level, fieldCircleSet));
            return list;

        } else {    //reference Type

            Map<String, Object> map = new LinkedHashMap<>();

            PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(type);

            if (psiClass == null) {
                return map;
            }

            if (psiClass.isEnum()) { // enum

                for (PsiField field : psiClass.getFields()) {
                    if (field instanceof PsiEnumConstant) {
                        return field.getName();
                    }
                }
                return "";

            } else {

                java.util.List<String> fieldTypeNames = new ArrayList<>();

                PsiType[] types = type.getSuperTypes();

                fieldTypeNames.add(type.getPresentableText());
                fieldTypeNames.addAll(Arrays.stream(types).map(PsiType::getPresentableText).collect(Collectors.toList()));

                if (fieldTypeNames.stream().anyMatch(s -> s.startsWith("Collection") || s.startsWith("Iterable"))) {// Iterable

                    java.util.List<Object> list = new ArrayList<>();
                    PsiType deepType = PsiUtil.extractIterableTypeParameter(type, false);
                    list.add(typeResolve(deepType, level, fieldCircleSet));
                    return list;

                } else { // Object

                    List<String> retain = new ArrayList<>(fieldTypeNames);
                    retain.retainAll(normalTypes.keySet());
                    if (!retain.isEmpty()) {
                        return normalTypes.get(retain.get(0));
                    } else {
                        // 同一个类型循环调用，只生成一次
                        if(fieldCircleSet.contains(type.toString())){
                            return null;
                        }
                        fieldCircleSet.add(type.toString());
                        if (level > 500) {
                            throw new SequenceOutlineException("This class reference level exceeds maximum limit or has nested references!");
                        }

                        for (PsiField field : psiClass.getAllFields()) {
                            map.put(field.getName(), typeResolve(field.getType(), level, fieldCircleSet));
                        }

                        return map;
                    }
                }
            }
        }
    }


    public static Object getDefaultValue(PsiType type) {
        if (!(type instanceof PsiPrimitiveType)) return null;
        switch (type.getCanonicalText()) {
            case "boolean":
                return false;
            case "byte":
                return (byte) 0;
            case "char":
                return '\0';
            case "short":
                return (short) 0;
            case "int":
                return 0;
            case "long":
                return 0L;
            case "float":
                return zero;
            case "double":
                return zero;
            default:
                return null;
        }
    }

    public static Object genDefault(PsiType type) {
        switch (type.getCanonicalText()) {
            case "boolean":
            case "java.lang.Boolean":
                return false;
            case "byte":
            case "java.lang.Byte":
                return (byte) 0;
            case "char":
            case "java.lang.Character":
                return '\0';
            case "short":
            case "java.lang.Short":
                return (short) 0;
            case "int":
            case "java.lang.Integer":
                return 0;
            case "long":
            case "java.lang.Long":
                return 0L;
            case "float":
            case "java.lang.Float":
                return zero;
            case "double":
            case "java.lang.Double":
                return zero;
            case "java.lang.String":
                return "str";
            default:
                return null;
        }
    }
}
