package com.muy.common.actions.toolbaractions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.tabs.TabInfo;
import com.muy.view.component.AbstractSequenceOutlineSetTab;
import com.muy.view.component.SequenceOutlineTabBar;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class CloseTabAction extends AnAction {
    private AbstractSequenceOutlineSetTab abstractToolSetTab;

    public CloseTabAction(AbstractSequenceOutlineSetTab abstractToolSetTab) {
        super("Close Tab", "Close selected Tab", AllIcons.Actions.Close);
        this.abstractToolSetTab = abstractToolSetTab;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        SequenceOutlineTabBar sequenceOutlineTabBar = abstractToolSetTab.getToolSetTabBar();
        TabInfo tabInfoSelect = sequenceOutlineTabBar.getSelectedInfo();
        if(null != tabInfoSelect){
            sequenceOutlineTabBar.removeTab(tabInfoSelect);
        }
    }
}
