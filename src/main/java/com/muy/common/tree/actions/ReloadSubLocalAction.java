package com.muy.common.tree.actions;

import com.intellij.icons.AllIcons;
import com.muy.common.tree.MTTreeCell;
import com.muy.common.tree.TreePanelMark;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 12:45
 */
public class ReloadSubLocalAction extends AbstractMTTreeCellSelectTreeAction{

    public ReloadSubLocalAction(TreePanelMark treePanelMark) {
        super("ReloadSubLocal", "reload Sub Local", AllIcons.General.Remove, treePanelMark);
    }

    @Override
    public void handleSelectTree(DefaultMutableTreeNode mutableTreeNode, MTTreeCell mtTreeCell) {
        mtTreeCell.reloadSubLocal(treePanelMark, mutableTreeNode);
    }
}
