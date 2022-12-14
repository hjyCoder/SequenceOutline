package com.muy.common.actions;

import com.google.common.collect.ImmutableSortedMap;
import com.google.gson.JsonNull;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.actions.CompareFilesAction;
import com.intellij.diff.chains.DiffRequestChain;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.contents.DocumentContentImpl;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.requests.ErrorDiffRequest;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.icons.AllIcons;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.muy.common.utils.JacksonUtils;
import com.muy.common.utils.JsonUtils;
import com.muy.view.panel.PanelCompare;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class CompareJsonDiffActionClick extends CompareFilesAction {

    public static final String ACTION_TEXT = "Json Diff";

    private PanelCompare panelCompare;

    public CompareJsonDiffActionClick(PanelCompare panelCompare) {
        super();
        this.panelCompare = panelCompare;
        init();
    }

    public void init(){
        getTemplatePresentation().setText(ACTION_TEXT);
        getTemplatePresentation().setIcon(AllIcons.Diff.Compare4LeftRight);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        DiffRequestChain chain = getDiffRequestChain(e);
        if (chain == null) return;

        DiffManager.getInstance().showDiff(project, chain, DiffDialogHints.NON_MODAL);
    }



    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        VirtualFile[] files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);

        String text = (files != null && files.length == 1) ? "Compare JSON File With..." : "Compare JSON Files";

        e.getPresentation().setText(text);
    }

    @Override
    protected boolean isAvailable(@NotNull AnActionEvent e) {
        return true;
    }

    @Override
    protected DiffRequest getDiffRequest(@NotNull AnActionEvent e) {
        try {
            Editor left = panelCompare.getLeftEditor();
            Editor right = panelCompare.getRightEditor();
            return new SimpleDiffRequest("A VS B", content(left.getDocument().getText(), e.getProject()), content(right.getDocument().getText(), e.getProject()), "A", "B");
        } catch (IOException | RuntimeException exception) {
            exception.printStackTrace();
            return new ErrorDiffRequest("Problem with some of JSON files");
        }
    }

    private Map<String, Object> deepSort(Map<?, ?> map) {
        ImmutableSortedMap.Builder<String, Object> builder = ImmutableSortedMap.naturalOrder();

        map.forEach((k, v) -> {
            if (v instanceof Map) {
                builder.put((String) k, deepSort((Map) v));
            } else {
                builder.put((String) k, v == null ? JsonNull.INSTANCE : v);
            }
        });
        return builder.build();
    }

    private DiffContent content(String jsonContent, Project project) throws IOException {
        String sortJson = JacksonUtils.sortJson(jsonContent);
        String prettyJsonString = JsonUtils.formatJson(sortJson);
        return new DocumentContentImpl( project, new DocumentImpl(prettyJsonString), JsonFileType.INSTANCE);
    }
}
