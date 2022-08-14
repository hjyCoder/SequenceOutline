package com.muy.common.tree.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbService;
import com.muy.common.exception.SequenceOutlineException;
import com.muy.common.notification.SequenceOutlineNotifier;
import com.muy.common.tree.MTTreeCell;
import com.muy.common.tree.TreePanelMark;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @Author jiyanghuang
 * @Date 2022/8/5 01:56
 */
public class SaveRightContentAction extends AbstractMTTreeCellAction {

    public SaveRightContentAction(TreePanelMark treePanelMark) {
        super("SaveRightContent", "保存右边内容", AllIcons.General.Add, treePanelMark);
    }

    /**
     * 保存时每个节点维护各自的内容
     *
     * @param e
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            DefaultMutableTreeNode mutableTreeNode = (DefaultMutableTreeNode) treePanelMark.jTree().getLastSelectedPathComponent();
            if (null == mutableTreeNode) {
                mutableTreeNode = treePanelMark.rootTreeNode();
            }
            final DefaultMutableTreeNode mutableTreeNodeT = mutableTreeNode;
            if (mutableTreeNodeT.getUserObject() instanceof MTTreeCell) {
                DumbService.getInstance(e.getProject()).runWhenSmart(() -> {
                    MTTreeCell mtTreeCell = (MTTreeCell) mutableTreeNodeT.getUserObject();
                    mtTreeCell.saveRightContent();
                });
            } else {
                SequenceOutlineNotifier.notify("Invalid treeNodeSelect");
            }
        } catch (SequenceOutlineException sequenceOutlineException) {
            if (null != sequenceOutlineException.getResponseCode()) {
                SequenceOutlineNotifier.notify(sequenceOutlineException.getResponseCode().getDesc());
                return;
            }
            SequenceOutlineNotifier.notify("error");
        } catch (Exception ex) {
            SequenceOutlineNotifier.notify(ex.getMessage());
        }
    }
}
