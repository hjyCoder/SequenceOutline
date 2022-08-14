package com.muy.common.tree.enums;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 20:06
 */
public enum AddSubTreeNodeType {

    JSON("通过复制粘贴复制板的内容获取子元素信息"),
    DIALOG("通过Dialog的方式填充内容");

    private String desc;

    AddSubTreeNodeType(String desc){
        this.desc = desc;
    }
}
