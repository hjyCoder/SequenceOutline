package com.muy.common.tree.actions;

import com.intellij.icons.AllIcons;
import com.muy.common.tree.MTTreeCell;
import com.muy.common.tree.TreePanelMark;
import com.muy.utils.JTreeUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @Author jiyanghuang
 * @Date 2022/8/11 22:56
 */
public class DeleteAllChildTreeNodeAction extends AbstractMTTreeCellSelectTreeAction{

    public DeleteAllChildTreeNodeAction(TreePanelMark treePanelMark) {
        super("Delete all child Node", "Delete all child", AllIcons.Actions.DeleteTag, treePanelMark);
    }

    public DeleteAllChildTreeNodeAction(@Nullable String text, @Nullable String description, TreePanelMark treePanelMark) {
        super(text, description, AllIcons.General.Remove, treePanelMark);
    }

    @Override
    public void handleSelectTree(DefaultMutableTreeNode mutableTreeNode, MTTreeCell mtTreeCell) {
        mtTreeCell.removeAllChildrenTreeNode(mutableTreeNode);
        JTreeUtils.reload(treePanelMark.jTree(), mutableTreeNode);
    }
}
