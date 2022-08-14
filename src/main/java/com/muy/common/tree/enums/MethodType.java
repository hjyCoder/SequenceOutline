package com.muy.common.tree.enums;

import com.intellij.icons.AllIcons;
import lombok.Getter;

import javax.swing.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/8 23:26
 */
public enum MethodType {

    INTERFACE_METHOD(0, "接口方法", AllIcons.Nodes.Interface),
    ABSTRACT_METHOD(1, "抽象类方法", AllIcons.Nodes.AbstractClass),
    NORMAL_METHOD(2, "普通类方法", AllIcons.Nodes.Class),
    ANONYMOUS_CLASS_METHOD(3, "匿名类方法", AllIcons.Nodes.AnonymousClass),
    RECURSIVE_METHOD(4, "递归调用方法", AllIcons.Gutter.RecursiveMethod),
    ;

    @Getter
    private int type;
    private String desc;
    private Icon icon;

    MethodType(int type, String desc, Icon icon) {
        this.type = type;
        this.desc = desc;
        this.icon = icon;
    }

    public static MethodType value(int type) {
        for (MethodType methodType : values()) {
            if (methodType.type == type) {
                return methodType;
            }
        }
        return MethodType.NORMAL_METHOD;
    }

    public static Icon typeIcon(int type) {
        return value(type).icon;
    }
}
