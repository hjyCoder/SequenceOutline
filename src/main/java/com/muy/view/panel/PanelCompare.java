package com.muy.view.panel;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.JBUI;
import com.muy.common.actions.CompareJsonDiffActionClick;
import com.muy.view.panel.json.JsonPathEvaluateSnippetView;

import javax.swing.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/4 20:55
 */
public class PanelCompare extends JSplitPane {

    private static final double WINDOW_WEIGHT = 0.9D;

    private Project project;

    private JsonPathEvaluateSnippetView leftEditor;

    private JsonPathEvaluateSnippetView rightEditor;


    public PanelCompare(Project project){
        this.project = project;

        rightEditor = new JsonPathEvaluateSnippetView(project);

        leftEditor = new JsonPathEvaluateSnippetView(project);

        setContinuousLayout(true);
        setResizeWeight(WINDOW_WEIGHT);
        setDividerSize(2);
        setBorder(JBUI.Borders.empty());
        setLeftComponent(leftEditor);
        setRightComponent(rightEditor);

        rightEditor.rightClickActionGroup().add(new CompareJsonDiffActionClick(PanelCompare.this));
    }

    public Editor getLeftEditor() {
        return leftEditor.getSourceEditor();
    }

    public Editor getRightEditor() {
        return rightEditor.getSourceEditor();
    }
}
