package com.muy.common.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.WriteAction;
import com.intellij.util.ThrowableRunnable;
import com.muy.common.notification.EditorHintsNotifier;
import com.muy.common.utils.JsonUtils;
import com.muy.view.panel.json.JsonPathEvaluateSnippetView;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * FormatJsonAction
 *
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class FormatJsonViewAction extends AnAction {

    public static final String ACTION_TEXT = "Expand Json";

    private JsonPathEvaluateSnippetView jsonPathEvaluateSnippetView;

    public FormatJsonViewAction(JsonPathEvaluateSnippetView jsonPathEvaluateSnippetView) {
        super(ACTION_TEXT, ACTION_TEXT, AllIcons.Actions.Expandall);
        this.jsonPathEvaluateSnippetView = jsonPathEvaluateSnippetView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            WriteAction.run(new ThrowableRunnable<Throwable>() {
                @Override
                public void run() throws Throwable {
                    String formattedJson = JsonUtils.formatJson(jsonPathEvaluateSnippetView.text());
                    jsonPathEvaluateSnippetView.getSourceEditor().getDocument().setText(formattedJson);
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