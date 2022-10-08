package com.muy.view.panel.json;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.impl.ContextMenuPopupHandler;
import com.muy.common.actions.DialogEditorDateAction;
import com.muy.common.actions.DialogEditorEscapeJsonAction;
import com.muy.common.actions.MRGotoLineAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @Author jiyanghuang
 * @Date 2022/8/27 13:40
 */
public class MRJsonPopupHandler extends ContextMenuPopupHandler {

    @Override
    public @Nullable ActionGroup getActionGroup(@NotNull EditorMouseEvent event) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new DialogEditorEscapeJsonAction());
        actionGroup.add(new DialogEditorDateAction());
        actionGroup.add(new MRGotoLineAction());
        return actionGroup;
    }
}
