package com.muy.common.tree;

import com.intellij.openapi.project.Project;
import com.muy.common.tab.TabJsonWrap;
import com.muy.utils.JacksonUtils;
import lombok.Getter;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 12:32
 */
public class TreeRightPanelJson extends MRTreeRightPanel {

    @Getter
    private TabJsonWrap tabJsonWrap;

    public TreeRightPanelJson(Project project, Object data) {
        super(project);
        tabJsonWrap = new TabJsonWrap(project, 0, "JsonView", tabbedPane, JacksonUtils.toFormatJSONString(data), false);
    }
}
