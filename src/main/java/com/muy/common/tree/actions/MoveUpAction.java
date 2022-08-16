package com.muy.common.tree.actions;

import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.muy.common.tree.MTTreeCell;
import com.muy.common.tree.TreePanelMark;
import com.muy.utils.JTreeUtils;
import com.muy.utils.MRListUtils;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

/**
 * @Author jiyanghuang
 * @Date 2022/8/11 22:56
 */
public class MoveUpAction extends AbstractMTTreeCellSelectTreeAction {

    public MoveUpAction(TreePanelMark treePanelMark) {
        super("moveUp", "moveUp", AllIcons.Actions.MoveUp, treePanelMark);
    }

    @Override
    public void handleSelectTree(DefaultMutableTreeNode mutableTreeNode, MTTreeCell mtTreeCell) {
        if (!mtTreeCell.canMoveUp(mutableTreeNode)) {
            return;
        }

        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) mutableTreeNode.getParent();
        List<DefaultMutableTreeNode> allChild = Lists.newArrayList();
        int pos = -1;
        for (int i = 0; i < parent.getChildCount(); i++) {
            DefaultMutableTreeNode childT = (DefaultMutableTreeNode) parent.getChildAt(i);
            allChild.add(childT);
            if (childT.equals(mutableTreeNode)) {
                pos = i;
            }
        }
        MRListUtils.swrap(allChild, pos - 1, pos);
        parent.removeAllChildren();
        for (DefaultMutableTreeNode child : allChild) {
            parent.add(child);
        }
        mtTreeCell.moveUp(parent, mutableTreeNode, (MTTreeCell) parent.getUserObject(), pos);
        JTreeUtils.reload(treePanelMark.jTree(), parent);
    }
}
