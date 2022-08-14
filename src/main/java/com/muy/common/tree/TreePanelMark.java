package com.muy.common.tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @Author jiyanghuang
 * @Date 2022/7/17 20:50
 */
public interface TreePanelMark {

    public default DefaultMutableTreeNode rootTreeNode() {
        return null;
    }

    public default JTree jTree() {
        return null;
    }

    /**
     * 是否展示右边内容
     * 默认展示
     *
     * @return
     */
    public default boolean showRightContent() {
        return true;
    }
}
