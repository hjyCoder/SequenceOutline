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
public class TreeExpandAction extends AnAction implements DumbAware {

    private final JTree tree;

    public TreeExpandAction(JTree tree){
        super("Expand all", "打开所有树节点", AllIcons.Actions.Expandall);
        this.tree = tree;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreePath treePath = tree.getSelectionPath();
        if(null != treePath){
            JTreeUtils.expandOrCollapAll(tree, treePath, true);
        }
    }
}
