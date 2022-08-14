package com.muy.view.component;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.TabsListener;
import com.intellij.ui.tabs.impl.JBEditorTabs;
import com.muy.common.actions.toolbaractions.CloseTabAction;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class SequenceOutlineTabBar extends JBEditorTabs implements TabsListener {
    private final Project project;
    private AtomicLong atomicLong;

    public SequenceOutlineTabBar(@NotNull Project project) {
        super(project, IdeFocusManager.findInstance(), (Disposable)project);
        this.project = project;
        this.addListener((TabsListener)this);
        this.setTabDraggingEnabled(true);
        atomicLong = new AtomicLong();
    }

    public final void addTab(AbstractSequenceOutlineSetTab abstractToolSetTab) {
        DefaultActionGroup closeActionGroup = new DefaultActionGroup();
        closeActionGroup.add((AnAction)(new CloseTabAction(abstractToolSetTab)));
        String tabId = "Tab" + atomicLong.getAndIncrement();
        TabInfo tabInfo = new TabInfo(abstractToolSetTab.subJComponent(tabId));
        tabInfo.setText(tabId);
        tabInfo.setIcon(AllIcons.General.Web);
        tabInfo.setTabLabelActions((ActionGroup)closeActionGroup, "EditorTab");
        this.addTab(tabInfo);
        this.select(tabInfo, true);
    }

    @Override
    public void selectionChanged(TabInfo oldSelection, TabInfo newSelection) {
        System.out.println(" tabChange -->" + newSelection.getText());
        SequenceOutlineComponent.getInstance(project);
    }

    @Override
    public void tabRemoved(@NotNull TabInfo tabToRemove) {
        SequenceOutlineComponent.getInstance(project).removeRegister(tabToRemove.getText());
    }
}
