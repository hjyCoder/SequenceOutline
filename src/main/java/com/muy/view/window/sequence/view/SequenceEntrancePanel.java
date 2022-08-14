package com.muy.view.window.sequence.view;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBTabbedPane;
import com.muy.common.tab.TabJsonWrap;
import com.muy.common.utils.JacksonUtils;
import com.muy.service.filters.config.FilterConfig;
import com.muy.view.window.sequence.bean.TreeNodeModelSequence;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/6 14:07
 */
public class SequenceEntrancePanel extends JPanel {

    private Project project;

    private JTabbedPane tabbedPane;

    private TabWrapSequenceFilter tabShowConfig;

    private TabWrapSequenceFilter tabScanConfig;

    @Getter
    private TabJsonWrap tabJsonWrap;

    @Getter
    private TreeNodeModelSequence treeNodeModelSequence;

    public SequenceEntrancePanel(Project project, TreeNodeModelSequence treeNodeModelSequence) {
        this.project = project;
        this.treeNodeModelSequence = treeNodeModelSequence;

        initView();
    }

    private void initView() {
        setLayout(new BorderLayout(0, 0));
        tabbedPane = new JTabbedPane(JBTabbedPane.TOP);
        tabShowConfig = new TabWrapSequenceFilter(project, 0, "ShowConfig", tabbedPane, new SequenceFilter(FilterConfig.ofDefault(treeNodeModelSequence.getFilterConfigShow())));
        tabScanConfig = new TabWrapSequenceFilter(project, 1, "ScanConfig", tabbedPane, new SequenceFilter(FilterConfig.ofDefault(treeNodeModelSequence.getFilterConfig())));
        tabJsonWrap = new TabJsonWrap(project, 2, "JsonView", tabbedPane, JacksonUtils.toFormatJSONString(treeNodeModelSequence));
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void saveConfig() {
        tabShowConfig.getSequenceFilter().saveConfig();
        tabScanConfig.getSequenceFilter().saveConfig();
    }

    public void updateConfigUI(){
        tabShowConfig.getSequenceFilter().fillContent();
        tabScanConfig.getSequenceFilter().fillContent();
    }
}
