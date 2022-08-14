package com.muy.view.window.sequence.view;

import com.intellij.openapi.project.Project;
import com.muy.common.tab.MRTabWrap;
import lombok.Getter;

import javax.swing.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/6 14:11
 */
public class TabWrapSequenceFilter extends MRTabWrap {

    @Getter
    private SequenceFilter sequenceFilter;

    public TabWrapSequenceFilter(Project project, int index, String title, JTabbedPane tabbedPane, SequenceFilter sequenceFilter) {
        super(project, index, title, tabbedPane, sequenceFilter);
        this.sequenceFilter = sequenceFilter;
    }
}
