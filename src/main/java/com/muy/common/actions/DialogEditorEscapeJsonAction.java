package com.muy.common.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.muy.common.dialog.DialogFormJson;
import com.muy.common.dialog.MRDialog;
import com.muy.common.notification.SequenceOutlineNotifier;
import com.muy.common.utils.JsonUtils;
import com.muy.utils.JsonCommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;

/**
 * @Author jiyanghuang
 * @Date 2022/8/28 01:32
 */
public class DialogEditorEscapeJsonAction extends AnAction {

    public static final String ACTION_TEXT = "InvokeEscapeJsonDialog";


    public DialogEditorEscapeJsonAction(){
        super(ACTION_TEXT, ACTION_TEXT, AllIcons.FileTypes.Json);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        Editor editor = EDITOR.getData(dataContext);
        if(null == editor){
            return;
        }
        String text = editor.getSelectionModel().getSelectedText();
        if(StringUtils.isBlank(text)){
            SequenceOutlineNotifier.notifyError("select text is blank");
            return;
        }
        boolean[] containQuotation = {false};
        if (text.startsWith("\"") && text.endsWith("\"")) {
            containQuotation[0] = true;
            text = text.substring(1, text.length() - 1);
        }
        if(!JsonCommonUtils.isEscapeJson(text)){
            SequenceOutlineNotifier.notifyError("select text is not escapeJson");
            return;
        }
        text = JsonCommonUtils.unescapeJson(text);
        text = JsonUtils.formatJsonWrap(text);
        DialogFormJson dialogFormJson = new DialogFormJson(editor.getProject(), text, (fv) -> {
            fv = JsonUtils.minifyJsonWrap(fv);
            fv = JsonCommonUtils.escapeJson(fv);
            if (containQuotation[0]) {
                fv = "\"" + fv + "\"";
            }
            final String fvFinal = fv;
            Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
            int start = primaryCaret.getSelectionStart();
            int end = primaryCaret.getSelectionEnd();
            WriteCommandAction.runWriteCommandAction(editor.getProject(), () ->
                    editor.getDocument().replaceString(start, end, fvFinal)
            );
        });
        MRDialog.of(dialogFormJson).show();
    }
}
