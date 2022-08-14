package com.muy.view.component;

import com.intellij.openapi.project.Project;
import com.muy.view.window.sequence.SequenceTabContent;

import javax.swing.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/4 20:28
 */
public class SequenceTab extends AbstractSequenceOutlineSetTab {

    public static final String NAME = "Sequence";

    public SequenceTab(Project project){
        super(project);
    }
    @Override
    public String tabName() {
        return NAME;
    }

    @Override
    public JComponent subJComponent(String tabId) {
        SequenceTabContent sequenceTabContent = new SequenceTabContent(project, this, tabId);
        return sequenceTabContent;
    }
}
