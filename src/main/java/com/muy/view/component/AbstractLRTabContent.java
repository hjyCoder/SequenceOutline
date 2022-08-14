package com.muy.view.component;

import com.intellij.openapi.project.Project;
import com.intellij.util.ui.JBUI;

import javax.swing.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public abstract class AbstractLRTabContent<L extends JComponent, R extends JComponent> extends JSplitPane {

    private final Project project;

    private final AbstractSequenceOutlineSetTab abstractToolSetTab;

    private final String tabId;

    private static final double WINDOW_WEIGHT = 0.17D;

    protected L l;

    protected R r;

    public AbstractLRTabContent(Project project, L l, R r, AbstractSequenceOutlineSetTab abstractToolSetTab, String tabId) {
        super(HORIZONTAL_SPLIT);
        this.project = project;
        this.l = l;
        this.r = r;
        this.abstractToolSetTab = abstractToolSetTab;
        this.tabId = tabId;

        // JSplitPane 分割窗口参数
        setContinuousLayout(true);
        setResizeWeight(WINDOW_WEIGHT);
        // 如果设置为0则无法再移动
        setDividerSize(1);
        setBorder(JBUI.Borders.empty());

        setLeftComponent(this.l);
        setRightComponent(this.r);

        SequenceOutlineComponent.getInstance(project).register(abstractToolSetTab, tabId, r.getClass(), this.r);
    }
}
