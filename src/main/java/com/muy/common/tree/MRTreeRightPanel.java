package com.muy.common.tree;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBTabbedPane;

import javax.swing.*;
import java.awt.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 12:29
 */
public abstract class MRTreeRightPanel extends JPanel {

    protected Project project;

    protected JTabbedPane tabbedPane;

    public MRTreeRightPanel(Project project) {
        this.project = project;

        setLayout(new BorderLayout(0, 0));
        tabbedPane = new JTabbedPane(JBTabbedPane.TOP);
        add(tabbedPane, BorderLayout.CENTER);
    }
}
