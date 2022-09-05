package com.muy.common.dialog;

import com.intellij.featureStatistics.FeatureUsageTracker;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actions.CopyAction;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @Author jiyanghuang
 * @Date 2022/8/28 10:44
 */
public class MREditorWriteActionHandler extends EditorActionHandler {


    @Setter
    private String valueUpdate;

    /**
     * todo 后面研究如何更新比较严谨
     *
     * @param editor
     * @param caret
     * @param dataContext
     */
    @Override
    protected void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
        if (!editor.getSelectionModel().hasSelection(true)) {
            if (CopyAction.isSkipCopyPasteForEmptySelection()) {
                return;
            }
        }
        ApplicationManager.getApplication().runWriteAction(() -> {
            FeatureUsageTracker.getInstance().triggerFeatureUsed("editing.copy.line");
//            editor.getCaretModel().runForEachCaret(__ -> {
//                editor.getSelectionModel().selectLineAtCaret();
//                EditorActionUtil.moveCaretToLineStartIgnoringSoftWraps(editor);
//            });
            SelectionModel selectionModel = editor.getSelectionModel();
            editor.getCaretModel().runForEachCaret(new CaretAction() {
                @Override
                public void perform(Caret caret) {
                    editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), valueUpdate);
                }
            });
        });
    }
}
