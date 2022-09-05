package com.muy.common.dialog;

import com.intellij.jsonpath.ui.JsonPathEvaluateSnippetView;
import com.intellij.openapi.project.Project;
import com.muy.view.panel.json.JsonPathEvaluateSnippetViewJava;

import javax.swing.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/28 00:32
 */
public class DialogFormJson implements DialogFormMark {

    private Project project;

    private JsonPathEvaluateSnippetViewJava jsonEditor;

    public DialogFormJson(Project project){
        jsonEditor = new JsonPathEvaluateSnippetViewJava(project);
    }

    @Override
    public JComponent jComponent() {
        return jsonEditor;
    }

    public void fillText(String value){
        jsonEditor.setSource(value);
    }

    public String fetchText(){
        return jsonEditor.text();
    }
}
