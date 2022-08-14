package com.muy.view.component;

import com.intellij.openapi.project.Project;
import com.muy.view.panel.PanelCompare;

import javax.swing.*;

public class JsonTab extends AbstractSequenceOutlineSetTab{

    public static final String NAME = "Json";

    private JSplitPane jsonTabContent;

    private JPanel jsonTabHead;

    public JsonTab(Project project){
        super(project);
    }


    @Override
    public String tabName() {
        return NAME;
    }

    @Override
    public JComponent subJComponent(String tabId) {
        PanelCompare panelCompare = new PanelCompare(project);
        return panelCompare;
    }

    public JSplitPane getJsonTabContent() {
        return jsonTabContent;
    }

    public JPanel getJsonTabHead() {
        return jsonTabHead;
    }


}
