package com.muy.common.tree.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbService;
import com.muy.common.notification.SequenceOutlineNotifier;
import com.muy.common.tree.MTTreeCell;
import com.muy.common.tree.TreePanelMark;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 按选择的树节点进行处理
 *
 * @Author jiyanghuang
 * @Date 2022/8/6 20:51
 */
public abstract class AbstractMTTreeCellSelectTreeAction extends AbstractMTTreeCellAction {

    public AbstractMTTreeCellSelectTreeAction(@Nullable String text, @Nullable String description, @Nullable Icon icon, TreePanelMark treePanelMark) {
        super(text, description, icon, treePanelMark);
    }

    @Override
    public void actionPerformedWrap(@NotNull AnActionEvent e) {
        DefaultMutableTreeNode mutableTreeNode = (DefaultMutableTreeNode) treePanelMark.jTree().getLastSelectedPathComponent();
        if (null == mutableTreeNode) {
            mutableTreeNode = treePanelMark.rootTreeNode();
        }
        final DefaultMutableTreeNode mutableTreeNodeT = mutableTreeNode;
        if (mutableTreeNodeT.getUserObject() instanceof MTTreeCell) {
            DumbService.getInstance(e.getProject()).runWhenSmart(() -> {
                MTTreeCell mtTreeCell = (MTTreeCell) mutableTreeNodeT.getUserObject();
                handleSelectTree(mutableTreeNodeT, mtTreeCell);
            });
        } else {
            SequenceOutlineNotifier.notify("Invalid treeNodeSelect");
        }
    }

    /**
     * 由子类来实现具体操作，选中的树节点
     *
     * @param mutableTreeNode
     * @param mtTreeCell
     */
    public void handleSelectTree(DefaultMutableTreeNode mutableTreeNode, MTTreeCell mtTreeCell) {

    }
}
