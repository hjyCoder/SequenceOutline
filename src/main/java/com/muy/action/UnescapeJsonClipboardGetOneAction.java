package com.muy.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.muy.utils.ClipboardUtils;
import com.muy.utils.JsonLogUtils;
import org.jetbrains.annotations.NotNull;

/**
 * SortJsonAction
 */
public class UnescapeJsonClipboardGetOneAction extends AnAction {

    public static final String ACTION_TEXT = "Unescape json from clipboard Get 1";

    public UnescapeJsonClipboardGetOneAction() {
        super(ACTION_TEXT, ACTION_TEXT, AllIcons.FileTypes.Json);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        String result = JsonLogUtils.jsons("").get(1).toString();
        ClipboardUtils.fillStringToClip(result);
    }
}
