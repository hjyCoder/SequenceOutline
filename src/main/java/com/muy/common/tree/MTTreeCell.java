package com.muy.common.tree;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.SimpleTextAttributes;
import com.muy.common.tree.enums.AddSubTreeNodeType;
import com.muy.utils.ActionUtil;
import com.muy.utils.ClipboardUtils;
import com.muy.utils.TreeUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

/**
 * @Author jiyanghuang
 * @Date 2022/7/17 19:54
 */
public interface MTTreeCell<T extends MTTreeCell> {

    public static Map<MTTreeCell, Icon> loaindIconMap = Maps.newHashMapWithExpectedSize(120);

    /**
     * 展示的名称
     *
     * @return
     */
    public String cellShow();

    /**
     * 树节点展示的名称
     *
     * @return
     */
    public default Icon iconSelected() {
        return null;
    }

    /**
     * @return
     */
    public default Icon iconUnselected() {
        return null;
    }

    /**
     * 是否是组件自己控制选择状态
     * 然后根据选择状态来展示图片
     *
     * @return
     */
    public default Boolean selfControlSelect() {
        return null;
    }

    /**
     * 数据加载时动画
     *
     * @return
     */
    public default Icon loadingIcon() {
        return loaindIconMap.get(this);
    }

    /**
     * 获取项目对象
     *
     * @return
     */
    public default Project project() {
        return null;
    }

    /**
     * 设置字段的颜色
     *
     * @return
     */
    public default SimpleTextAttributes fontAttributes() {
        return null;
    }

    /**
     * 树型节点被选择时
     */
    public default void treeSelectionListener(JTree tree, DefaultMutableTreeNode mutableTreeNode, TreePanelMark treePanelMark) {

    }

    /**
     * 右键显示
     */
    public default void rightClick(JTree tree, DefaultMutableTreeNode mutableTreeNode, MouseEvent event, TreePanelMark treePanelMark) {
        TreePath path = tree.getSelectionPath();
        if (path != null) {
            DefaultActionGroup defaultActionGroup = new DefaultActionGroup();
            ActionGroup actionGroup = rightClickActionGroup(tree, mutableTreeNode, defaultActionGroup, treePanelMark);
            if (actionGroup != null) {
                ActionPopupMenu actionPopupMenu = ActionUtil.createActionPopupMenu(tree, "", actionGroup);
                JPopupMenu popupMenu = actionPopupMenu.getComponent();
                popupMenu.show(tree, event.getX(), event.getY());
            }
        }
    }

    /**
     * 右键时可以执行哪些操作
     * 如果返回空对象则右击无Action
     *
     * @param tree
     * @param mutableTreeNode
     * @param defaultActionGroup 可以不按默认返回，而新创建一个ActionGroup
     * @return
     */
    public default ActionGroup rightClickActionGroup(JTree tree, DefaultMutableTreeNode mutableTreeNode, DefaultActionGroup defaultActionGroup, TreePanelMark treePanelMark) {
        return null;
    }

    /**
     * 双击
     */
    public default void doubleClick(JTree tree, DefaultMutableTreeNode mutableTreeNode, TreePanelMark treePanelMark) {

    }

    /**
     * 按回车键
     */
    public default void keyEnter(JTree tree, DefaultMutableTreeNode mutableTreeNode, TreePanelMark treePanelMark) {

    }

    public default MTTreeCell parent() {
        return null;
    }

    public default List<MTTreeCell> subTreeCell() {
        return null;
    }

    /**
     * 直接从粘贴板复制内容，直接生成子节点
     *
     * @param tree
     * @param mutableTreeNode
     */
    public default void addSubTreeNode(JTree tree, DefaultMutableTreeNode mutableTreeNode, TreePanelMark treePanelMark) {
        if (AddSubTreeNodeType.DIALOG.equals(addSubTreeNodeType())) {
            addSubTreeNodeDialog(tree, mutableTreeNode, treePanelMark);
            return;
        }
        DefaultMutableTreeNode childNode = buildSubTreeNode(ClipboardUtils.fetchStringFromClip(), treePanelMark);
        addSubTreeNode(tree, mutableTreeNode, treePanelMark, childNode);
    }

    public default void addSubTreeNode(JTree tree, DefaultMutableTreeNode mutableTreeNode, TreePanelMark treePanelMark, DefaultMutableTreeNode childNode) {
        if (null == childNode) {
            return;
        }
        mutableTreeNode.add(childNode);
        ((DefaultTreeModel) tree.getModel()).reload(mutableTreeNode);
    }

    public default AddSubTreeNodeType addSubTreeNodeType() {
        return AddSubTreeNodeType.JSON;
    }

    public default void addSubTreeNodeDialog(JTree tree, DefaultMutableTreeNode mutableTreeNode, TreePanelMark treePanelMark) {

    }

    /**
     * 默认需要构建一个对象返回并添加到子树当中
     *
     * @param jsonStr
     * @return
     */
    public default DefaultMutableTreeNode buildSubTreeNode(String jsonStr, TreePanelMark treePanelMark) {
        return null;
    }


    /**
     * 保存树及子树的信息信息
     */
    public default void saveTreeContent(TreePanelMark treePanelMark) {

    }

    /**
     * 保存右边信息即单个节点的信息
     */
    public default void saveRightContent() {

    }

    /**
     * 保存右边信息即单个节点的信息
     * 到项目本地
     */
    public default void saveRightContentLocal() {

    }

    /**
     * 保存右边信息即单个节点的信息
     */
    public default void makeSceneEffect() {

    }

    /**
     * 删除树节点，删除哪些内容由树节点
     */
    public default void treeDelete() {

    }

    /**
     * 删除树节点信息，只是删除本地的节点
     * 不涉及互远程
     * Project 按项目本地的方式
     */
    public default void treeDeleteLocal(TreePanelMark treePanelMark, DefaultMutableTreeNode mutableTreeNode) {

    }

    /**
     * 根据保存在本地的信息加载子节点
     *
     * @param treePanelMark
     * @param mutableTreeNode
     */
    public default void reloadSubLocal(TreePanelMark treePanelMark, DefaultMutableTreeNode mutableTreeNode) {

    }

    public default void parentChildHandle(T parent, T sub) {
        List<MTTreeCell> subList = parent.subTreeCell();
        if (null != subList) {
            subList.add(sub);
        }
    }

    /**
     * 重新子节点
     *
     * @param tree
     * @param mutableTreeNode
     * @param showList
     */
    public default void reload(JTree tree, DefaultMutableTreeNode mutableTreeNode, List<MTTreeCell> showList) {
        mutableTreeNode.removeAllChildren();
        for (MTTreeCell mtTreeCell : showList) {
            Pair<MTTreeCell, DefaultMutableTreeNode> root = TreeUtils.treeConvert(mtTreeCell, t -> new DefaultMutableTreeNode(t), MTTreeCell::subTreeCell, (r, s) -> r.add(s));
            mutableTreeNode.add(root.getRight());
        }
        ((DefaultTreeModel) tree.getModel()).reload(mutableTreeNode);
    }

    /**
     * 根节点不允许删除
     *
     * @param tree
     * @param mutableTreeNode
     */
    public default void removeCurrent(JTree tree, DefaultMutableTreeNode mutableTreeNode) {
        TreeNode treeNode = mutableTreeNode.getParent();
        if (null == treeNode || (!(treeNode instanceof DefaultMutableTreeNode))) {
            return;
        }
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) treeNode;
        parentNode.remove(mutableTreeNode);
        ((DefaultTreeModel) tree.getModel()).reload(parentNode);
    }

    /**
     * 根据输入结果进行搜索
     *
     * @param treePanelMark
     * @param mutableTreeNode
     * @param keyword
     */
    public default void searchKeyword(TreePanelMark treePanelMark, DefaultMutableTreeNode mutableTreeNode, String keyword) {

    }

    /**
     * 复制右边的内容，一般是配置项
     *
     * @param treePanelMark
     * @param mutableTreeNode
     */
    public default void copyRightContent(TreePanelMark treePanelMark, DefaultMutableTreeNode mutableTreeNode) {

    }

    /**
     * 粘贴右边的内容，一般是配置项
     *
     * @param treePanelMark
     * @param mutableTreeNode
     */
    public default void pasteRightContent(TreePanelMark treePanelMark, DefaultMutableTreeNode mutableTreeNode) {

    }

    public default void removeCurrentTreeNode(DefaultMutableTreeNode nodeParent, DefaultMutableTreeNode nodeCurrent, MTTreeCell cellParent) {
        // 删除树节点
        if (nodeCurrent.getChildCount() > 0) {
            List<DefaultMutableTreeNode> currentChilds = Lists.newArrayList();
            for (int i = 0; i < nodeCurrent.getChildCount(); i++) {
                currentChilds.add((DefaultMutableTreeNode) nodeCurrent.getChildAt(i));
            }
            List<DefaultMutableTreeNode> parentChilds = Lists.newArrayList();
            for (int i = 0; i < nodeParent.getChildCount(); i++) {
                DefaultMutableTreeNode parentChildT = (DefaultMutableTreeNode) nodeParent.getChildAt(i);
                if (nodeCurrent.equals(parentChildT)) {
                    parentChilds.addAll(currentChilds);
                }
                parentChilds.add(parentChildT);
            }
            nodeParent.removeAllChildren();
            for (DefaultMutableTreeNode parentChild : parentChilds) {
                nodeParent.add(parentChild);
            }
        } else {
            nodeParent.remove(nodeCurrent);
        }
        TreeUtils.removeCurrentNode(this, cellParent, MTTreeCell::subTreeCell);
    }

    /**
     * @param nodeCurrent
     */
    public default void removeAllChildrenTreeNode(DefaultMutableTreeNode nodeCurrent) {
        nodeCurrent.removeAllChildren();
        List<MTTreeCell> subs = subTreeCell();
        if (CollectionUtils.isNotEmpty(subs)) {
            subs.clear();
        }
    }
}
