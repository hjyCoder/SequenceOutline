package com.muy.common.tree;

import lombok.Data;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @Author jiyanghuang
 * @Date 2022/7/18 01:03
 */
@Data
public class TreeCellEventExeContext {
    private JTree tree;
    private DefaultMutableTreeNode mutableTreeNode;

    public static TreeCellEventExeContext of(JTree tree, DefaultMutableTreeNode mutableTreeNode) {
        TreeCellEventExeContext exeContext = new TreeCellEventExeContext();
        exeContext.tree = tree;
        exeContext.mutableTreeNode = mutableTreeNode;
        return exeContext;
    }
}
