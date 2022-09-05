package com.muy.common.actions;

import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.muy.common.dialog.MREditorWriteActionHandler;
import lombok.Getter;

/**
 * @Author jiyanghuang
 * @Date 2022/8/28 01:32
 */
public class DialogUpdateJsonAction extends EditorAction {

    @Getter
    private MREditorWriteActionHandler defaultHandler;

    public DialogUpdateJsonAction() {
        super(null);
        defaultHandler = new MREditorWriteActionHandler();
        this.setupHandler(defaultHandler);
    }

}
