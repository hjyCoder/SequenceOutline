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
public class DeleteCurrentAndChildTreeNodeAction extends AbstractMTTreeCellSelectTreeAction{

    public DeleteCurrentAndChildTreeNodeAction(TreePanelMark treePanelMark) {
        super("Delete current And Child Node", "Delete current And Child Node", AllIcons.Actions.DeleteTag, treePanelMark);
    }

    public DeleteCurrentAndChildTreeNodeAction(@Nullable String text, @Nullable String description, TreePanelMark treePanelMark) {
        super(text, description, AllIcons.General.Remove, treePanelMark);
    }

    @Override
    public void handleSelectTree(DefaultMutableTreeNode mutableTreeNode, MTTreeCell mtTreeCell) {
        if (null == mutableTreeNode.getParent()) {
            return;
        }
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) mutableTreeNode.getParent();
        MTTreeCell parentCell = (MTTreeCell) parent.getUserObject();
        mtTreeCell.removeAllChildrenTreeNode(mutableTreeNode);
        mtTreeCell.removeCurrentTreeNode(parent, mutableTreeNode, parentCell);
        JTreeUtils.reload(treePanelMark.jTree(), parent);
    }
}
