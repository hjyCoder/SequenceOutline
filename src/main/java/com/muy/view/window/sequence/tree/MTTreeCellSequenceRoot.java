package com.muy.view.window.sequence.tree;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.muy.common.tree.MTTreeCell;
import com.muy.common.tree.TreePanelMark;
import com.muy.common.tree.actions.ReloadSubLocalAction;
import com.muy.common.tree.treeinvoke.TreeInvokeTreeCell;
import com.muy.common.utils.CheckJsonValidUtils;
import com.muy.domain.bean.invoketree.TreeInvokeModel;
import com.muy.domain.bean.invoketree.TreeNodeModel;
import com.muy.utils.JTreeUtils;
import com.muy.view.component.SequenceOutlineComponent;
import com.muy.view.component.TabContentRightShow;
import com.muy.view.window.sequence.bean.TreeNodeModelSequence;
import com.muy.view.window.sequence.configuration.SequenceConfiguration;
import org.apache.commons.collections4.CollectionUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

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
    public Icon iconSelected() {
        return AllIcons.Hierarchy.Subtypes;
    }

    @Override
    public Icon iconUnselected() {
        return AllIcons.Hierarchy.Subtypes;
    }

    @Override
    public DefaultMutableTreeNode buildSubTreeNode(String jsonStr, TreePanelMark treePanelMark) {
        TreeNodeModelSequence treeNodeModel = CheckJsonValidUtils.toJavaObject(jsonStr, TreeNodeModelSequence.class, TreeNodeModelSequence::jsonConvertValid);
        MTTreeCellSequenceEntrance sequenceEntrance = new MTTreeCellSequenceEntrance(project, treeNodeModel);
        DefaultMutableTreeNode sequenceTreeNode = new DefaultMutableTreeNode(sequenceEntrance);

        TreeNodeModel treeNodeModelRoot = CheckJsonValidUtils.toJavaObject(jsonStr, TreeNodeModel.class, TreeNodeModel::jsonConvertCheck);
        TreeInvokeModel rootInvoke = TreeInvokeModel.of(treeNodeModelRoot);
        TreeInvokeTreeCell treeInvokeTreeCell = new TreeInvokeTreeCell(project, treeNodeModelRoot, rootInvoke);
        treeNodeModel.setRoot(rootInvoke);

        // 包装成树节点
        DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode(treeInvokeTreeCell);
        sequenceTreeNode.add(rootTreeNode);
        return sequenceTreeNode;
    }

    @Override
    public void treeSelectionListener(JTree tree, DefaultMutableTreeNode mutableTreeNode, TreePanelMark treePanelMark) {
        TabContentRightShow tabContentRightShow = SequenceOutlineComponent.getInstance(project).findInstance(TabContentRightShow.class);
        tabContentRightShow.updatePanel(null);
    }

    @Override
    public ActionGroup rightClickActionGroup(JTree tree, DefaultMutableTreeNode mutableTreeNode, DefaultActionGroup defaultActionGroup, TreePanelMark treePanelMark) {
        defaultActionGroup.add(new ReloadSubLocalAction(treePanelMark, "reload Entrance", "reload Entrance"));
        return defaultActionGroup;
    }

    @Override
    public void reloadSubLocal(TreePanelMark treePanelMark, DefaultMutableTreeNode mutableTreeNode) {
        SequenceConfiguration sequenceConfiguration = SequenceConfiguration.getInstance(project);
        List<TreeNodeModelSequence> entrances = sequenceConfiguration.getEntrances();
        treePanelMark.rootTreeNode().removeAllChildren();
        if(CollectionUtils.isNotEmpty(entrances)){
            for(TreeNodeModelSequence treeNodeModelSequence : entrances){
                MTTreeCellSequenceEntrance sequenceEntrance = new MTTreeCellSequenceEntrance(project, treeNodeModelSequence);
                DefaultMutableTreeNode sequenceTreeNode = new DefaultMutableTreeNode(sequenceEntrance);
                treePanelMark.rootTreeNode().add(sequenceTreeNode);
            }
        }
        JTreeUtils.reload(treePanelMark.jTree(), treePanelMark.rootTreeNode());
    }
}
