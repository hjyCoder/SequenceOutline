package com.muy.view.window.sequence.tree;

import com.intellij.openapi.project.Project;
import com.muy.common.tree.MTTreeCell;
import com.muy.common.tree.TreePanelMark;
import com.muy.common.utils.CheckJsonValidUtils;
import com.muy.view.component.SequenceOutlineComponent;
import com.muy.view.component.TabContentRightShow;
import com.muy.view.window.sequence.bean.TreeNodeModelSequence;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @Author jiyanghuang
 * @Date 2022/8/5 00:07
 */
public class MTTreeCellSequenceRoot implements MTTreeCell {

    private Project project;

    public MTTreeCellSequenceRoot(Project project) {
        this.project = project;
    }

    @Override
    public String cellShow() {
        return "SequenceRoot";
    }

    @Override
    public DefaultMutableTreeNode buildSubTreeNode(String jsonStr, TreePanelMark treePanelMark) {
        TreeNodeModelSequence treeNodeModel = CheckJsonValidUtils.toJavaObject(jsonStr, TreeNodeModelSequence.class, TreeNodeModelSequence::jsonConvertValid);
        MTTreeCellSequenceEntrance sequenceEntrance = new MTTreeCellSequenceEntrance(project, treeNodeModel);
        DefaultMutableTreeNode sequenceTreeNode = new DefaultMutableTreeNode(sequenceEntrance);
        return sequenceTreeNode;
    }

    @Override
    public void treeSelectionListener(JTree tree, DefaultMutableTreeNode mutableTreeNode, TreePanelMark treePanelMark) {
        TabContentRightShow tabContentRightShow = SequenceOutlineComponent.getInstance(project).findInstance(TabContentRightShow.class);
        tabContentRightShow.updatePanel(null);
    }
}
