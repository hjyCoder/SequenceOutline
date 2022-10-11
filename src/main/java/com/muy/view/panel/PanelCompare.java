package com.muy.view.panel;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.JBUI;
import com.muy.common.actions.CompareJsonDiffActionClick;
import com.muy.common.actions.CompareJsonDiffAllSortActionClick;
import com.muy.view.panel.json.JsonPathEvaluateSnippetViewJava;

import javax.swing.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/4 20:55
 */
public class PanelCompare extends JSplitPane {

    private static final double WINDOW_WEIGHT = 0.9D;

    private Project project;

    private JsonPathEvaluateSnippetViewJava leftEditor;

    private JsonPathEvaluateSnippetViewJava rightEditor;

//    private JsonPathEvaluateSnippetView rightEditor;

    public PanelCompare(Project project){
        this.project = project;

        rightEditor = new JsonPathEvaluateSnippetViewJava(project);

        leftEditor = new JsonPathEvaluateSnippetViewJava(project);

        setContinuousLayout(true);
        setResizeWeight(WINDOW_WEIGHT);
        setDividerSize(2);
        setBorder(JBUI.Borders.empty());
        setLeftComponent(leftEditor);
        setRightComponent(rightEditor);

        rightEditor.rightClickActionGroup().add(new CompareJsonDiffActionClick(PanelCompare.this));
        rightEditor.rightClickActionGroup().add(new CompareJsonDiffAllSortActionClick(PanelCompare.this));

    }

    public Editor getLeftEditor() {
        return leftEditor.getSourceEditor();
    }

    public Editor getRightEditor() {
        return rightEditor.getSourceEditor();
//        return null;
    }

    public void fillSource(String source) {
        leftEditor.setSource(source);
    }
}
