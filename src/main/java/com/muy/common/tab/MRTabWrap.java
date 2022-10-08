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

    public MRTabWrap(Project project, int index, String title, JTabbedPane tabbedPane, JComponent jComponent, boolean jbScrollPaneWrap) {
        this.project = project;
        this.index = index;
        this.title = title;
        this.tabbedPane = tabbedPane;

        if(null != jComponent){
            updateJComponent(jComponent, jbScrollPaneWrap);
        }
    }

    public void updateJComponent(JComponent jComponent, boolean jbScrollPaneWrap){
        if(jbScrollPaneWrap){
            JBScrollPane jbScrollPane = new JBScrollPane();
            jbScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            tabbedPane.add(title, jbScrollPane);
            jbScrollPane.setViewportView(jComponent);
        }else{
            tabbedPane.add(title, jComponent);
        }
    }

    public void selected(){
        tabbedPane.setSelectedIndex(index);
    }
}
