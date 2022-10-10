package com.muy.common.tab;

import com.intellij.openapi.project.Project;
import com.muy.view.panel.json.JsonPathEvaluateSnippetViewJava;

import javax.swing.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 11:00
 */
public class TabJsonWrap extends MRTabWrap {

    private JsonPathEvaluateSnippetViewJava jsonView;

    public TabJsonWrap(Project project, int index, String title, JTabbedPane tabbedPane, String jsonContent, boolean jbScrollPaneWrap) {
        super(project, index, title, tabbedPane, null, jbScrollPaneWrap);
        jsonView = new JsonPathEvaluateSnippetViewJava(project);
        updateJComponent(jsonView, false);
        fillSource(jsonContent);
    }

    public String text() {
        return jsonView.getSourceEditor().getDocument().getText();
    }

    public void fillSource(String source) {
        jsonView.setSource(source);
    }

    public void fillSourceSelect(String source) {
        jsonView.setSource(source);
        selected();
    }
}
