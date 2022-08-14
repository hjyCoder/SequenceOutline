package com.muy.common.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public interface IWindowAction {

    public String actionText();

    public Icon actionIcon();

    public void before(@NotNull AnActionEvent e);

    public void execute(@NotNull AnActionEvent e);

    public void after(@NotNull AnActionEvent e);

}
