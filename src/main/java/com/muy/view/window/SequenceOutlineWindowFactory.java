package com.muy.view.window;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.muy.view.component.SequenceOutlineComponent;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @Author jiyanghuang
 * @Date 2022/8/4 20:55
 */
public class SequenceOutlineWindowFactory implements ToolWindowFactory {

    public static Project project;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setToHideOnEmptyContent(true);
        SequenceOutlineComponent component = SequenceOutlineComponent.getInstance(project);
        component.setToolWindow(toolWindow);
        component.initRestComponent(toolWindow);
        initAction(toolWindow);
    }

    private void initAction(ToolWindow toolWindow) {
        List<AnAction> actions = Lists.newArrayList();
        ToolWindowEx ex = (ToolWindowEx) toolWindow;

        toolWindow.setTitleActions(actions);
    }
}
