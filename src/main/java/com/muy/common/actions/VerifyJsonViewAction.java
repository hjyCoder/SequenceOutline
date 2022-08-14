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
 * VerifyJsonAction
 *
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class VerifyJsonViewAction extends AnAction {

    public static final String VALID_JSON_MSG = "Valid Json";

    public static final String INVALID_JSON_MSG = "Invalid Json";

    public static final String ACTION_TEXT = "Verify Json";

    private JsonPathEvaluateSnippetView jsonPathEvaluateSnippetView;


    public VerifyJsonViewAction(JsonPathEvaluateSnippetView jsonPathEvaluateSnippetView){
        super(VALID_JSON_MSG, ACTION_TEXT, AllIcons.Actions.SetDefault);
        this.jsonPathEvaluateSnippetView = jsonPathEvaluateSnippetView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            WriteAction.run(new ThrowableRunnable<Throwable>() {
                @Override
                public void run() throws Throwable {
                    JsonUtils.verifyJson(jsonPathEvaluateSnippetView.text());
                }
            });
        } catch (JsonProcessingException jsonProcessingException) {
            String originalMessage = jsonProcessingException.getOriginalMessage();
            originalMessage = INVALID_JSON_MSG.concat(": ").concat(originalMessage);
            long charOffset = jsonProcessingException.getLocation().getCharOffset();

            EditorHintsNotifier.notifyError(Objects.requireNonNull(jsonPathEvaluateSnippetView.getSourceEditor()), originalMessage, charOffset);
            return;
        }catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        EditorHintsNotifier.notifyInfo(Objects.requireNonNull(jsonPathEvaluateSnippetView.getSourceEditor()), VALID_JSON_MSG);
    }
}