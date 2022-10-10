package com.muy.common.tree.treeinvoke;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.muy.common.tree.MTTreeCell;
import com.muy.common.tree.TreePanelMark;
import com.muy.common.tree.TreeRightPanelJson;
import com.muy.common.tree.TreeRightReflectInvokePanelJson;
import com.muy.common.tree.actions.*;
import com.muy.common.tree.enums.MethodType;
import com.muy.common.utils.CheckJsonValidUtils;
import com.muy.domain.bean.invoketree.TreeInvokeModel;
import com.muy.domain.bean.invoketree.TreeNodeModel;
import com.muy.utils.GoToSourceUtils;
import com.muy.utils.MRListUtils;
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
    @Getter
    private TreeNodeModel treeNodeModel;

    private TreeRightReflectInvokePanelJson treeRightPanelJson;

    public TreeInvokeTreeCell(Project project, TreeNodeModel treeNodeModel, TreeInvokeModel treeInvokeModel) {
        this.project = project;
        this.treeNodeModel = treeNodeModel;
        this.treeInvokeModel = treeInvokeModel;
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
        if(canMoveUp(mutableTreeNode)){
            defaultActionGroup.add(new MoveUpAction(treePanelMark));
        }
        if(canMoveDown(mutableTreeNode)){
            defaultActionGroup.add(new MoveDownAction(treePanelMark));
        }
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
            nodeParent.removeAllChildren();
        } else if (cellParent instanceof TreeInvokeTreeCell) {
            TreeInvokeTreeCell parentNode = (TreeInvokeTreeCell) cellParent;
            TreeUtils.removeCurrentNode(treeInvokeModel, parentNode.getTreeInvokeModel(), TreeInvokeModel::getSubInvoke);
            MTTreeCell.super.removeCurrentTreeNode(nodeParent, nodeCurrent, cellParent);
        }
    }

    @Override
    public void moveUp(DefaultMutableTreeNode nodeParent, DefaultMutableTreeNode nodeCurrent, MTTreeCell cellParent, int pos) {
        if (cellParent instanceof TreeInvokeTreeCell) {
            TreeInvokeTreeCell parentInvoke = (TreeInvokeTreeCell) cellParent;
            MRListUtils.swrap(parentInvoke.getTreeInvokeModel().getSubInvoke(), pos - 1, pos);
        }
        MTTreeCell.super.moveUp(nodeParent, nodeCurrent, cellParent, pos);
    }

    @Override
    public void moveDown(DefaultMutableTreeNode nodeParent, DefaultMutableTreeNode nodeCurrent, MTTreeCell cellParent, int pos) {
        if (cellParent instanceof TreeInvokeTreeCell) {
            TreeInvokeTreeCell parentInvoke = (TreeInvokeTreeCell) cellParent;
            MRListUtils.swrap(parentInvoke.getTreeInvokeModel().getSubInvoke(), pos, pos + 1);
        }
        MTTreeCell.super.moveDown(nodeParent, nodeCurrent, cellParent, pos);
    }

    @Override
    public void treeSelectionListener(JTree tree, DefaultMutableTreeNode mutableTreeNode, TreePanelMark treePanelMark) {
        try {
            if (null == treeRightPanelJson) {
                treeRightPanelJson = new TreeRightReflectInvokePanelJson(project, treeNodeModel);
            }
            TabContentRightShow tabContentRightShow = SequenceOutlineComponent.getInstance(project).findInstance(TabContentRightShow.class);
            tabContentRightShow.updatePanel(treeRightPanelJson);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void doubleClick(JTree tree, DefaultMutableTreeNode mutableTreeNode, TreePanelMark treePanelMark) {
        if (TreeNodeModel.SCHEME_JAVA.equals(treeNodeModel.getScheme())) {
            GoToSourceUtils.openMethodInEditor(project, treeNodeModel.fClassName(), treeNodeModel.findMethodName(), treeNodeModel.getMethodSignature());
        } else if (TreeNodeModel.SCHEME_LAMBDA.equals(treeNodeModel.getScheme())) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) mutableTreeNode.getParent();
            int pos = -1;
            for (int i = 0; i < parent.getChildCount(); i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
                if (!(child.getUserObject() instanceof TreeInvokeTreeCell)) {
                    continue;
                }
                TreeInvokeTreeCell invokeTreeCellChild = (TreeInvokeTreeCell) child.getUserObject();
                if (treeNodeModel.sameLambda(invokeTreeCellChild.getTreeNodeModel())) {
                    pos++;
                }
                // 遇到当前节点说明已经找到
                if (child.equals(mutableTreeNode)) {
                    break;
                }
            }
            GoToSourceUtils.openLambdaMethodInEditor(project, treeNodeModel.fClassName(), treeNodeModel.findMethodName().split("_")[0], treeNodeModel.getEncloseMethodSignature(), pos, treeNodeModel.getMethodSignature());
        }
    }

    @Override
    public DefaultMutableTreeNode buildSubTreeNode(String jsonStr, TreePanelMark treePanelMark) {
        TreeNodeModel treeNodeModelSub = CheckJsonValidUtils.toJavaObject(jsonStr, TreeNodeModel.class, TreeNodeModel::jsonConvertCheck);
        TreeInvokeModel treeInvokeModelSub = TreeInvokeModel.of(treeNodeModelSub);
        treeInvokeModel.getSubInvoke().add(treeInvokeModelSub);

        TreeInvokeTreeCell treeInvokeTreeCellSub = new TreeInvokeTreeCell(project, treeNodeModelSub, treeInvokeModelSub);
        return new DefaultMutableTreeNode(treeInvokeTreeCellSub);
    }
}
