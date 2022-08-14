package com.muy.common.actions.toolbaractions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.muy.view.component.AbstractSequenceOutlineSetTab;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class AddTabAction extends AnAction {

    private AbstractSequenceOutlineSetTab abstractToolSetTab;


    public AddTabAction(AbstractSequenceOutlineSetTab abstractToolSetTab) {
        super("Add Tab", "Create a new Tab", AllIcons.General.Add);
        this.abstractToolSetTab = abstractToolSetTab;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        abstractToolSetTab.addTab();
    }
}
