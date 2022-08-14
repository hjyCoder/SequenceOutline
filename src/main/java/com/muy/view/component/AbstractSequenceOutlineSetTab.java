package com.muy.view.component;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.muy.common.actions.toolbaractions.AddTabAction;
import com.muy.common.actions.toolbaractions.CloseTabAction;
import com.muy.view.window.SequenceOutlineWindowFactory;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public abstract class AbstractSequenceOutlineSetTab extends SimpleToolWindowPanel implements SequenceOutlineSetTab {

    protected Project project;

    private SequenceOutlineTabBar sequenceOutlineTabBar;

    public AbstractSequenceOutlineSetTab(Project project){
        super(false);
        this.project = project;
        ActionToolbar actionToolbar = createToolBar();
        actionToolbar.setTargetComponent(this);
        setToolbar(actionToolbar.getComponent());

        this.sequenceOutlineTabBar = new SequenceOutlineTabBar(project);
        this.sequenceOutlineTabBar.addTab(this);
        // 整个内容是 TabBar
        setContent(this.sequenceOutlineTabBar);
    }

    @Override
    public JPanel panelShow() {
        return this;
    }

    /**
     * create editor with specified language and content
     */
    protected FileEditor createEditor(String languageId, String text, Project project) {
        if (StringUtils.isEmpty(text)) {
            text = "";
        }
        try{
            Language language = Language.findLanguageByID(languageId);
            System.out.println("projectShow:" + SequenceOutlineWindowFactory.project);
            PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText(language, text);
            FileEditor editor = TextEditorProvider.getInstance().createEditor(project, psiFile.getVirtualFile());
            return editor;
        }catch(Exception ex){
            System.out.println("ex" + ex);
            return null;
        }

    }

    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * 创建工具条
     */
    private ActionToolbar createToolBar() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.addAll(
                new AddTabAction(this),
                new CloseTabAction(this)
        );
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, group, true);
        toolbar.setOrientation(SwingConstants.VERTICAL);
        return toolbar;
    }

    public abstract JComponent subJComponent(String tabId);

    public void addTab(){
        this.sequenceOutlineTabBar.addTab(this);
    }

    public SequenceOutlineTabBar getToolSetTabBar() {
        return sequenceOutlineTabBar;
    }

    public Project getProject() {
        return project;
    }
}
