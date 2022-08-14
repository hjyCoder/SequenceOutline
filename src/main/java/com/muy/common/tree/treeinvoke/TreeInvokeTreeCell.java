package com.muy.common.tree.treeinvoke;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.muy.common.tree.MTTreeCell;
import com.muy.common.tree.TreePanelMark;
import com.muy.common.tree.TreeRightPanelJson;
import com.muy.common.tree.actions.DeleteAllChildTreeNodeAction;
import com.muy.common.tree.actions.DeleteCurrentAndChildTreeNodeAction;
import com.muy.common.tree.actions.DeleteCurrentTreeNodeAction;
import com.muy.common.tree.enums.MethodType;
import com.muy.domain.bean.invoketree.TreeInvokeModel;
import com.muy.domain.bean.invoketree.TreeNodeModel;
import com.muy.utils.GoToSourceUtils;
import com.muy.utils.TreeUtils;
import com.muy.view.component.TabContentRightShow;
import com.muy.view.component.SequenceOutlineComponent;
import com.muy.view.window.sequence.tree.MTTreeCellSequenceEntrance;
import lombok.Getter;
import org.apache.commons.compress.utils.Lists;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 11:33
 */
public class TreeInvokeTreeCell implements MTTreeCell<TreeInvokeTreeCell> {

    private List<MTTreeCell> sub = Lists.newArrayList();

    private Project project;

    /**
     * 冗余一份方便删除子节点信息
     */
    @Getter
    private TreeInvokeModel treeInvokeModel;

    /**
     * 展示的节点信息
     */
    private TreeNodeModel treeNodeModel;

    private TreeRightPanelJson treeRightPanelJson;

    public TreeInvokeTreeCell(Project project, TreeNodeModel treeNodeModel) {
        this.project = project;
        this.treeNodeModel = treeNodeModel;
    }

    @Override
    public String cellShow() {
        return treeNodeModel.getClassName() + "." + treeNodeModel.getMethodName();
    }

    @Override
    public List<MTTreeCell> subTreeCell() {
        return sub;
    }

    @Override
    public Icon iconSelected() {
        return MethodType.typeIcon(treeNodeModel.getMethodType());
    }

    @Override
    public Icon iconUnselected() {
        return MethodType.typeIcon(treeNodeModel.getMethodType());
    }

    @Override
    public ActionGroup rightClickActionGroup(JTree tree, DefaultMutableTreeNode mutableTreeNode, DefaultActionGroup defaultActionGroup, TreePanelMark treePanelMark) {
        defaultActionGroup.add(new DeleteAllChildTreeNodeAction(treePanelMark));
        defaultActionGroup.add(new DeleteCurrentTreeNodeAction(treePanelMark));
        defaultActionGroup.add(new DeleteCurrentAndChildTreeNodeAction(treePanelMark));
        return defaultActionGroup;
    }

    @Override
    public void removeAllChildrenTreeNode(DefaultMutableTreeNode nodeCurrent) {
        treeInvokeModel.getSubInvoke().clear();
        MTTreeCell.super.removeAllChildrenTreeNode(nodeCurrent);
    }

    @Override
    public void removeCurrentTreeNode(DefaultMutableTreeNode nodeParent, DefaultMutableTreeNode nodeCurrent, MTTreeCell cellParent) {
        if (cellParent instanceof MTTreeCellSequenceEntrance) {
            MTTreeCellSequenceEntrance entrance = (MTTreeCellSequenceEntrance) cellParent;
            entrance.getTreeNodeModelSequence().setRoot(null);
        } else if (cellParent instanceof TreeInvokeTreeCell) {
            TreeInvokeTreeCell parentNode = (TreeInvokeTreeCell) cellParent;
            TreeUtils.removeCurrentNode(treeInvokeModel, parentNode.getTreeInvokeModel(), TreeInvokeModel::getSubInvoke);
        }
        MTTreeCell.super.removeCurrentTreeNode(nodeParent, nodeCurrent, cellParent);
    }

    @Override
    public void treeSelectionListener(JTree tree, DefaultMutableTreeNode mutableTreeNode, TreePanelMark treePanelMark) {
        try {
            if (null == treeRightPanelJson) {
                treeRightPanelJson = new TreeRightPanelJson(project, treeNodeModel);
            }
            TabContentRightShow tabContentRightShow = SequenceOutlineComponent.getInstance(project).findInstance(TabContentRightShow.class);
            tabContentRightShow.updatePanel(treeRightPanelJson);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void doubleClick(JTree tree, DefaultMutableTreeNode mutableTreeNode, TreePanelMark treePanelMark) {
        String methodNameFind = treeNodeModel.getMethodName();
        if(GoToSourceUtils.CONSTRUCTORS_METHOD_NAME.equals(methodNameFind)){
            methodNameFind = treeNodeModel.getClassName();
        }
        GoToSourceUtils.openMethodInEditor(project, treeNodeModel.fClassName(), methodNameFind, treeNodeModel.getMethodSignature());
    }
}
