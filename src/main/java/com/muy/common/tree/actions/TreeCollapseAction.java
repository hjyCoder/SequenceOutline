package com.muy.common.tree.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.muy.utils.JTreeUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreePath;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class TreeCollapseAction extends AnAction implements DumbAware {

    private final JTree tree;

    public TreeCollapseAction(JTree tree) {
        super("Collapse all", "收起所有树节点", AllIcons.Actions.Collapseall);
        this.tree = tree;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreePath treePath = tree.getSelectionPath();
        if (null != treePath) {
            JTreeUtils.expandOrCollapAll(tree, treePath, false);
        }
    }
}
