package com.muy.view.window.sequence;

import com.intellij.openapi.project.Project;
import com.muy.view.component.AbstractLRTabContent;
import com.muy.view.component.AbstractSequenceOutlineSetTab;
import com.muy.view.component.TabContentRightShow;

/**
 * @Author jiyanghuang
 * @Date 2022/8/4 20:55
 */
public class SequenceTabContent extends AbstractLRTabContent<SequenceLeft, TabContentRightShow> {

    public SequenceTabContent(Project project, AbstractSequenceOutlineSetTab abstractToolSetTab, String tabId) {
        super(project, new SequenceLeft(project), new TabContentRightShow(project, false), abstractToolSetTab, tabId);
        r.fillLeft(l);
    }
}
