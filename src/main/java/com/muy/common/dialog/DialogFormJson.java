package com.muy.common.dialog;

import com.intellij.jsonpath.ui.JsonPathEvaluateSnippetView;
import com.intellij.openapi.project.Project;
import com.muy.view.panel.json.JsonPathEvaluateSnippetViewJava;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @Author jiyanghuang
 * @Date 2022/8/28 00:32
 */
public class DialogFormJson implements DialogFormMark {

    private Project project;

    private JsonPathEvaluateSnippetViewJava jsonEditor;

    private Consumer<String> doUpdate;

    public DialogFormJson(Project project, String value, Consumer<String> doUpdate){
        jsonEditor = new JsonPathEvaluateSnippetViewJava(project);
        fillText(value);
        this.doUpdate = doUpdate;
    }

    @Override
    public JComponent jComponent() {
        return jsonEditor;
    }

    @Override
    public String title() {
        return "EditorJson";
    }

    @Override
    public Function<DialogFormJson, Pair<Boolean, String>> okFun() {
        return (form) -> {
            doUpdate.accept(form.fetchText());
            return Pair.of(true, null);
        };
    }

    public void fillText(String value){
        jsonEditor.setSource(value);
    }

    public String fetchText(){
        return jsonEditor.text();
    }
}
