package com.muy.view.component;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.muy.common.actions.toolbaractions.AddTabAction;
import com.muy.common.actions.toolbaractions.CloseTabAction;

import javax.swing.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public abstract class AbstractSequenceOutlineSetTab extends SimpleToolWindowPanel implements SequenceOutlineSetTab {

    private static final Logger LOGGER = Logger.getInstance(AbstractSequenceOutlineSetTab.class);

    protected Project project;

    private SequenceOutlineTabBar sequenceOutlineTabBar;

    public AbstractSequenceOutlineSetTab(Project project){
        super(false);
        this.project = project;
        ActionToolbar actionToolbar = createToolBar();
        actionToolbar.setTargetComponent(this);
        setToolbar(actionToolbar.getComponent());

        this.sequenceOutlineTabBar = new SequenceOutlineTabBar(project);
        this.sequenceOutlineTabBar.addTab(this);
        // 整个内容是 TabBar
        setContent(this.sequenceOutlineTabBar);
    }

    @Override
    public JPanel panelShow() {
        return this;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * 创建工具条
     */
    private ActionToolbar createToolBar() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.addAll(
                new AddTabAction(this),
                new CloseTabAction(this)
        );
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, group, true);
        toolbar.setOrientation(SwingConstants.VERTICAL);
        return toolbar;
    }

    public abstract JComponent subJComponent(String tabId);

    public void addTab(){
        this.sequenceOutlineTabBar.addTab(this);
    }

    public SequenceOutlineTabBar getToolSetTabBar() {
        return sequenceOutlineTabBar;
    }

    public Project getProject() {
        return project;
    }
}
