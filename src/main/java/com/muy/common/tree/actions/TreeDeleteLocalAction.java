package com.muy.common.tree.actions;

import com.intellij.icons.AllIcons;
import com.muy.common.tree.MTTreeCell;
import com.muy.common.tree.TreePanelMark;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @Author jiyanghuang
 * @Date 2022/8/6 20:45
 */
public class TreeDeleteLocalAction extends AbstractMTTreeCellSelectTreeAction {

    public TreeDeleteLocalAction(TreePanelMark treePanelMark) {
        super("DeleteLocal", "Delete Project Local", AllIcons.General.Remove, treePanelMark);
    }

    @Override
    public void handleSelectTree(DefaultMutableTreeNode mutableTreeNode, MTTreeCell mtTreeCell) {
        mtTreeCell.treeDeleteLocal(treePanelMark, mutableTreeNode);
    }
}
