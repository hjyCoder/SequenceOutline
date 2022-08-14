package com.muy.common.tree.actions;

import com.intellij.icons.AllIcons;
import com.muy.common.tree.MTTreeCell;
import com.muy.common.tree.TreePanelMark;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @Author jiyanghuang
 * @Date 2022/8/11 22:56
 */
public class CopyRightContentAction extends AbstractMTTreeCellSelectTreeAction{

    public CopyRightContentAction(TreePanelMark treePanelMark) {
        super("copy config", "copy config", AllIcons.Actions.Copy, treePanelMark);
    }

    public CopyRightContentAction(@Nullable String text, @Nullable String description, TreePanelMark treePanelMark) {
        super(text, description, AllIcons.Actions.Copy, treePanelMark);
    }

    @Override
    public void handleSelectTree(DefaultMutableTreeNode mutableTreeNode, MTTreeCell mtTreeCell) {
        mtTreeCell.copyRightContent(treePanelMark, mutableTreeNode);
    }
}
