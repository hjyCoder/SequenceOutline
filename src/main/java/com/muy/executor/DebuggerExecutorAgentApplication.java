package com.muy.executor;

import com.intellij.execution.Executor;
import com.intellij.openapi.wm.ToolWindowId;
import com.muy.constant.SequenceConstant;
import com.muy.utils.GuiUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class DebuggerExecutorAgentApplication extends Executor {

    @Override
    @NotNull
    public String getToolWindowId() {
        return ToolWindowId.DEBUG;
    }

    @Override
    @NotNull
    public Icon getToolWindowIcon() {
        return GuiUtils.DEBUG;
    }

    @Override
    @NotNull
    public Icon getIcon() {
        return GuiUtils.DEBUG;
    }

    @Override
    public Icon getDisabledIcon() {
        return null;
    }

    @Override
    public String getDescription() {
        return SequenceConstant.DEBUGGER_DESC;
    }

    @Override
    @NotNull
    public String getActionName() {
        return SequenceConstant.DEBUGGER_DESC;
    }

    @Override
    @NotNull
    public String getId() {
        return SequenceConstant.DEBUGGER_ID;
    }


    @Override
    @NotNull
    public String getStartActionText() {
        return SequenceConstant.DEBUGGER_DESC;
    }

    @Override
    @NotNull
    public String getStartActionText(@NotNull String configurationName) {
        return SequenceConstant.DEBUGGER_DESC;
    }

    @Override
    public String getContextActionId() {
        return SequenceConstant.DEBUGGER_ACTION_ID;
    }


    @Override
    public String getHelpId() {
        return null;
    }
}
