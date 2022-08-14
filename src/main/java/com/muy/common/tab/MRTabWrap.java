package com.muy.common.tab;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/6 14:12
 */
public abstract class MRTabWrap {

    private Project project;

    private int index;

    private String title;

    private JTabbedPane tabbedPane;

    public MRTabWrap(Project project, int index, String title, JTabbedPane tabbedPane, JComponent jComponent) {
        this.project = project;
        this.index = index;
        this.title = title;
        this.tabbedPane = tabbedPane;

        if(null != jComponent){
            updateJComponent(jComponent);
        }
    }

    public void updateJComponent(JComponent jComponent){
        JBScrollPane jbScrollPane = new JBScrollPane();
        jbScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tabbedPane.add(title, jbScrollPane);
        jbScrollPane.setViewportView(jComponent);
    }

    public void selected(){
        tabbedPane.setSelectedIndex(index);
    }
}
