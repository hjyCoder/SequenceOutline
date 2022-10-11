package com.muy.common.tab;

import com.intellij.openapi.project.Project;
import com.muy.view.panel.PanelCompare;

import javax.swing.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 11:00
 */
public class TabJsonPanelCompareWrap extends MRTabWrap {

    private PanelCompare panelCompare;

    public TabJsonPanelCompareWrap(Project project, int index, String title, JTabbedPane tabbedPane, String jsonContent, boolean jbScrollPaneWrap) {
        super(project, index, title, tabbedPane, null, jbScrollPaneWrap);
        panelCompare = new PanelCompare(project);
        updateJComponent(panelCompare, jbScrollPaneWrap);
        fillSource(jsonContent);
    }

    public void fillSource(String source) {
        panelCompare.fillSource(source);
    }

    public void fillSourceSelect(String source) {
        panelCompare.fillSource(source);
        selected();
    }
}
