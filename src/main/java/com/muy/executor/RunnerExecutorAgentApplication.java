package com.muy.executor;

import com.intellij.execution.Executor;
import com.intellij.openapi.wm.ToolWindowId;
import com.muy.constant.SequenceConstant;
import com.muy.utils.GuiUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class RunnerExecutorAgentApplication extends Executor {

    @Override
    @NotNull
    public String getToolWindowId() {
        return ToolWindowId.RUN;
    }

    @Override
    @NotNull
    public Icon getToolWindowIcon() {
        return GuiUtils.RUN;
    }

    @Override
    @NotNull
    public Icon getIcon() {
        return GuiUtils.RUN;
    }

    @Override
    public Icon getDisabledIcon() {
        return null;
    }

    @Override
    public String getDescription() {
        return SequenceConstant.RUNNER_DESC;
    }

    @Override
    @NotNull
    public String getActionName() {
        return SequenceConstant.RUNNER_DESC;
    }

    @Override
    @NotNull
    public String getId() {
        return SequenceConstant.RUNNER_ID;
    }


    @Override
    @NotNull
    public String getStartActionText() {
        return SequenceConstant.RUNNER_DESC;
    }

    @Override
    @NotNull
    public String getStartActionText(@NotNull String configurationName) {
        return SequenceConstant.RUNNER_DESC;
    }

    @Override
    public String getContextActionId() {
        return SequenceConstant.RUNNER_ACTION_ID;
    }


    @Override
    public String getHelpId() {
        return null;
    }
}
