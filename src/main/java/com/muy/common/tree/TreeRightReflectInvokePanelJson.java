package com.muy.common.tree;

import com.intellij.openapi.project.Project;
import com.muy.common.restful.TabRestfulReqWrap;
import com.muy.common.tab.TabJsonWrap;
import com.muy.domain.bean.invoketree.TreeNodeModel;
import com.muy.utils.JacksonUtils;
import lombok.Getter;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 12:32
 */
public class TreeRightReflectInvokePanelJson extends MRTreeRightPanel {

    @Getter
    private TabRestfulReqWrap tabRestfulReqWrap;

    @Getter
    private TabJsonWrap tabJsonWrap;

    public TreeRightReflectInvokePanelJson(Project project, TreeNodeModel treeNodeModel) {
        super(project);
        tabRestfulReqWrap = new TabRestfulReqWrap(project, 0, "ReflectInvoke", tabbedPane, false);
        tabRestfulReqWrap.fillReqParams(treeNodeModel.getReflectInvokeParam());
        tabRestfulReqWrap.fillDefaultData();
        tabJsonWrap = new TabJsonWrap(project, 0, "JsonView", tabbedPane, JacksonUtils.toFormatJSONString(treeNodeModel), false);
    }
}
