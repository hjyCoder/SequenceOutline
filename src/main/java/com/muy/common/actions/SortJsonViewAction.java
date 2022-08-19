package com.muy.common.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.WriteAction;
import com.intellij.util.ThrowableRunnable;
import com.muy.common.notification.EditorHintsNotifier;
import com.muy.common.utils.JacksonUtils;
import com.muy.common.utils.JsonUtils;
import com.muy.view.panel.json.JsonPathEvaluateSnippetViewJava;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * SortJsonAction
 *
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class SortJsonViewAction extends AnAction {

    public static final String ACTION_TEXT = "Sort Json";

    private JsonPathEvaluateSnippetViewJava jsonPathEvaluateSnippetView;


    public SortJsonViewAction(JsonPathEvaluateSnippetViewJava jsonPathEvaluateSnippetView) {
        super(ACTION_TEXT, ACTION_TEXT, AllIcons.ObjectBrowser.VisibilitySort);
        this.jsonPathEvaluateSnippetView = jsonPathEvaluateSnippetView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            WriteAction.run(new ThrowableRunnable<Throwable>() {
                @Override
                public void run() throws Throwable {
                    String minifiedJson = JsonUtils.formatJson(JacksonUtils.sortJson(jsonPathEvaluateSnippetView.text()));
                    jsonPathEvaluateSnippetView.getSourceEditor().getDocument().setText(minifiedJson);
                }
            });
        } catch (JsonProcessingException jsonProcessingException) {
            String originalMessage = jsonProcessingException.getOriginalMessage();
            long charOffset = jsonProcessingException.getLocation().getCharOffset();

            EditorHintsNotifier.notifyError(Objects.requireNonNull(jsonPathEvaluateSnippetView.getSourceEditor()), originalMessage, charOffset);
        }catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
