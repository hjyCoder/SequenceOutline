package com.muy.common.tree.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.NlsActions;
import com.muy.common.exception.SequenceOutlineException;
import com.muy.common.notification.SequenceOutlineNotifier;
import com.muy.common.tree.TreePanelMark;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/5 01:57
 */
public abstract class AbstractMTTreeCellAction extends AnAction implements DumbAware {

    protected TreePanelMark treePanelMark;

    public AbstractMTTreeCellAction(@Nullable @NlsActions.ActionText String text,
                                    @Nullable @NlsActions.ActionDescription String description,
                                    @Nullable Icon icon, TreePanelMark treePanelMark) {
        super(text, description, icon);
        this.treePanelMark = treePanelMark;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            actionPerformedWrap(e);
        } catch (SequenceOutlineException sequenceOutlineException) {
            if (null != sequenceOutlineException.getResponseCode()) {
                SequenceOutlineNotifier.notify(sequenceOutlineException.getResponseCode().getDesc());
                return;
            }
            SequenceOutlineNotifier.notify("error");
        } catch (Exception ex) {
            SequenceOutlineNotifier.notify(ex.getMessage());
        }
    }

    /**
     * 整个被异常处理包裹
     *
     * @param e
     */
    public void actionPerformedWrap(@NotNull AnActionEvent e) {

    }
}
