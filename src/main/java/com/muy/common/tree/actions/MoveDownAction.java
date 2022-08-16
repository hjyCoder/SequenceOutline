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
public class MoveDownAction extends AbstractMTTreeCellSelectTreeAction {

    public MoveDownAction(TreePanelMark treePanelMark) {
        super("moveDown", "moveDown", AllIcons.Actions.MoveDown, treePanelMark);
    }

    @Override
    public void handleSelectTree(DefaultMutableTreeNode mutableTreeNode, MTTreeCell mtTreeCell) {
        if (!mtTreeCell.canMoveDown(mutableTreeNode)) {
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
        MRListUtils.swrap(allChild, pos, pos + 1);
        parent.removeAllChildren();
        for (DefaultMutableTreeNode child : allChild) {
            parent.add(child);
        }
        mtTreeCell.moveDown(parent, mutableTreeNode, (MTTreeCell) parent.getUserObject(), pos);
        JTreeUtils.reload(treePanelMark.jTree(), parent);
    }
}
