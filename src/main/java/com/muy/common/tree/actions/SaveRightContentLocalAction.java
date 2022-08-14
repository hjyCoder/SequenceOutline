package com.muy.common.tree.actions;

import com.intellij.icons.AllIcons;
import com.muy.common.tree.MTTreeCell;
import com.muy.common.tree.TreePanelMark;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @Author jiyanghuang
 * @Date 2022/8/5 02:04
 */
public class SaveRightContentLocalAction extends AbstractMTTreeCellSelectTreeAction {

    public SaveRightContentLocalAction(TreePanelMark treePanelMark) {
        super("SaveRightContentLocal", "保存右边配置到项目配置", AllIcons.General.Add, treePanelMark);
    }

    @Override
    public void handleSelectTree(DefaultMutableTreeNode mutableTreeNode, MTTreeCell mtTreeCell) {
        mtTreeCell.saveRightContentLocal();
    }
}
