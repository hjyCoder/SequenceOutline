package com.muy.common.tree;

import com.intellij.ui.ColoredTreeCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class MTTreeCellRenderer extends ColoredTreeCellRenderer {

    /**
     * 渲染每个节点
     *
     * @param tree
     * @param value
     * @param selected
     * @param expanded
     * @param leaf
     * @param row
     * @param hasFocus
     */
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Object obj = ((DefaultMutableTreeNode) value).getUserObject();
        if (obj instanceof MTTreeCell) {
            MTTreeCell node = (MTTreeCell) obj;
            Icon loadingIcon = node.loadingIcon();
            if (null != loadingIcon) {
                setIcon(loadingIcon);
            } else if (null != node.selfControlSelect()) {
                if (node.selfControlSelect()) {
                    setIcon(node.iconSelected());
                } else {
                    setIcon(node.iconUnselected());
                }
            } else {
                if (selected) {
                    if (null != node.iconSelected()) {
                        setIcon(node.iconSelected());
                    }
                } else {
                    if (null != node.iconUnselected()) {
                        setIcon(node.iconUnselected());
                    }
                }
            }

            if (null == node.fontAttributes()) {
                append(node.cellShow());
            } else {
                append(node.cellShow(), node.fontAttributes());
            }
        }
    }
}
