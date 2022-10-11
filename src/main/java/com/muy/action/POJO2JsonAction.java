package com.muy.action;

import com.google.common.collect.Sets;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.muy.common.exception.SequenceOutlineException;
import com.muy.common.notification.SequenceOutlineNotifier;
import org.jetbrains.annotations.NonNls;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class POJO2JsonAction extends AnAction {

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

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        Project project = e.getProject();
        PsiElement elementAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass selectedClass = PsiTreeUtil.getContextOfType(elementAt, PsiClass.class);
        try {
            Map<String, Object> kv = getFields(selectedClass);
            String json = gsonBuilder.create().toJson(kv);
            StringSelection selection = new StringSelection(json);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            String message = "Convert " + selectedClass.getName() + " to JSON success, copied to clipboard.";
            SequenceOutlineNotifier.notify(message);


        } catch (SequenceOutlineException ex) {
            SequenceOutlineNotifier.notifyError(ex.getMessage());
        } catch (Exception ex) {
            SequenceOutlineNotifier.notifyError(ex.getMessage());
        }
    }


    private static Map<String, Object> getFields(PsiClass psiClass) {
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

            List<Object> list = new ArrayList<>();
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

                List<String> fieldTypeNames = new ArrayList<>();

                PsiType[] types = type.getSuperTypes();

                fieldTypeNames.add(type.getPresentableText());
                fieldTypeNames.addAll(Arrays.stream(types).map(PsiType::getPresentableText).collect(Collectors.toList()));

                if (fieldTypeNames.stream().anyMatch(s -> s.startsWith("Collection") || s.startsWith("Iterable"))) {// Iterable

                    List<Object> list = new ArrayList<>();
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
}


