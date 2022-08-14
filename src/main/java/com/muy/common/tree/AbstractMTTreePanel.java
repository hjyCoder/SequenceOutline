package com.muy.common.tree;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.muy.common.tree.actions.AddSubTreeNodeAction;
import com.muy.common.tree.actions.TreeCollapseAction;
import com.muy.common.tree.actions.TreeExpandAction;
import com.muy.utils.GuiUtils;
import com.muy.utils.JTreeUtils;
import org.jdesktop.swingx.JXTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

/**
 * @Author jiyanghuang
 * @Date 2022/7/17 20:54
 */
public abstract class AbstractMTTreePanel extends JPanel implements TreePanelMark {

    protected final Project project;

    private JPanel toolBarPanel;

    private JScrollPane treePanel;

    /**
     * 树 - service列表
     */
    private final JTree tree;

    protected DefaultMutableTreeNode rootTreeNode;

    private boolean rootVisible = true;

    public AbstractMTTreePanel(Project project) {
        this(project, true);
    }

    public AbstractMTTreePanel(Project project, boolean rootVisible) {
        this(project, rootVisible, null);
    }

    public AbstractMTTreePanel(Project project, boolean rootVisible, MTTreeCell mtTreeCellRoot) {
        this.project = project;
        toolBarPanel = new JPanel();
        this.tree = new JXTree();
        treePanel = new JScrollPane();
        this.rootVisible = rootVisible;

        // tree 配置
        JTreeUtils.configTree(tree, treePanel, rootVisible);

        //
        toolBarPanel.setLayout(new BorderLayout());
        setLayout(new BorderLayout());
        add(toolBarPanel, BorderLayout.NORTH);
        add(treePanel, BorderLayout.CENTER);
        installActions();
        initEvent();
        initTree(mtTreeCellRoot);
    }

    private void installActions() {

        DefaultActionGroup actionGroup = new DefaultActionGroup("MTTreeGroup", false);
        if (null != ApplicationManager.getApplication()) {
            actionGroup.add(new AddSubTreeNodeAction(this));
            actionGroup.addSeparator();
            actionGroup.add(new TreeExpandAction(tree));
            actionGroup.add(new TreeCollapseAction(tree));
//            actionGroup.add(new AddConfigAction());
//            actionGroup.addSeparator();
//            actionGroup.add(new DuplicateAction());
//            actionGroup.add(new SearchAction(tree));
            addActions(actionGroup);
        }

        GuiUtils.installActionGroupInToolBar(actionGroup, toolBarPanel, ActionManager.getInstance(), ActionPlaces.TOOLBAR, true);
    }

    protected void addActions(DefaultActionGroup actionGroup){

    }

    private void initEvent() {
        JTreeUtils.addTreeEvent(tree, this);
    }

    /**
     * 初始化树根节点
     */
    private void initTree(MTTreeCell mtTreeCellRoot) {
        rootTreeNode = JTreeUtils.renderRepeaterTree(tree, null == mtTreeCellRoot ? MTTreeCellBlankRoot.of() : mtTreeCellRoot);
    }

    @Override
    public DefaultMutableTreeNode rootTreeNode() {
        return rootTreeNode;
    }

    @Override
    public JTree jTree() {
        return tree;
    }
}
