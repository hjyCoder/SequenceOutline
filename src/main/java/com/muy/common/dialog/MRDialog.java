package com.muy.common.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/28 00:28
 */
public abstract class MRDialog<T extends DialogFormMark> extends DialogWrapper {

    protected T t;

    protected MRDialog(@Nullable Project project, boolean canBeParent, String title, T t) {
        super(project, canBeParent);
        setTitle(title);
        this.t = t;
        super.init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return t.jComponent();
    }
}
