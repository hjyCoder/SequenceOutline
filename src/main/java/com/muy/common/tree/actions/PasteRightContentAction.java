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
public class PasteRightContentAction extends AbstractMTTreeCellSelectTreeAction{

    public PasteRightContentAction(TreePanelMark treePanelMark) {
        super("Paste config", "Paste config", AllIcons.Actions.MenuPaste, treePanelMark);
    }

    public PasteRightContentAction(@Nullable String text, @Nullable String description, TreePanelMark treePanelMark) {
        super(text, description, AllIcons.Actions.MenuPaste, treePanelMark);
    }

    @Override
    public void handleSelectTree(DefaultMutableTreeNode mutableTreeNode, MTTreeCell mtTreeCell) {
        mtTreeCell.pasteRightContent(treePanelMark, mutableTreeNode);
    }
}
