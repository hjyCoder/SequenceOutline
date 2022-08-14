package com.muy.common.textfield;

import com.intellij.history.integration.ui.actions.ShowHistoryAction;
import com.intellij.ide.DataManager;
import com.intellij.jsonpath.JsonPathFileType;
import com.intellij.jsonpath.ui.JsonPathEvaluateManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.ActionButtonLook;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 21:49
 */
public class TfSearchWrapper extends NonOpaquePanel {

    public Project project;

    protected EditorTextField searchTextField;

    public TfSearchWrapper(Project project){
        this.project = project;

        searchTextField = new EditorTextField(project, JsonPathFileType.INSTANCE){
            @Override
            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && pressed) {
//                    evaluate();
                    return true;
                }
                return super.processKeyBinding(ks, e, condition, pressed);
            }

            @Override
            protected @NotNull EditorEx createEditor() {
                EditorEx editor = super.createEditor();

                editor.setBorder(JBUI.Borders.empty());
                editor.getComponent().setBorder(JBUI.Borders.empty(4, 0, 3, 6));
                editor.getComponent().setOpaque(false);
                editor.setBackgroundColor(UIUtil.getTextFieldBackground());

                PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
                if (psiFile != null) {
                    // JsonPathEvaluateManager.JSON_PATH_EVALUATE_SOURCE_KEY 这个key很重要，与后面提示获取json文件强关联
                    psiFile.putUserData(JsonPathEvaluateManager.JSON_PATH_EVALUATE_EXPRESSION_KEY, true);
//                    psiFile.putUserData(JsonPathEvaluateManager.JSON_PATH_EVALUATE_SOURCE_KEY, () -> getJsonFile());
                }
                return editor;
            }

        };

        SearchHistoryButton historyButton = new SearchHistoryButton(new ShowHistoryAction(), false);
        JPanel historyButtonWrapper = new NonOpaquePanel(new BorderLayout());
        historyButtonWrapper.setBorder(JBUI.Borders.empty(3, 6, 3, 6));
        historyButtonWrapper.add(historyButton, BorderLayout.NORTH);

        searchTextField.setFontInheritedFromLAF(false);

        // 设置属性
        add(historyButtonWrapper, BorderLayout.WEST);
        add(searchTextField, BorderLayout.CENTER);
        setBorder(JBUI.Borders.customLine(JBColor.border(), 0, 0, 1, 0));
        setOpaque(true);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        this.setBackground(UIUtil.getTextFieldBackground());
    }

    private class SearchHistoryButton extends ActionButton {
        SearchHistoryButton(AnAction action, Boolean focusable){
            super(action, action.getTemplatePresentation().clone(), ActionPlaces.UNKNOWN, ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE);
            setLook(ActionButtonLook.INPLACE_LOOK);
            this.setFocusable(focusable);
            updateIcon();
        }

        @Override
        protected DataContext getDataContext() {
            return DataManager.getInstance().getDataContext(this);
        }

        @Override
        public int getPopState() {
            if(isSelected()){
                return SELECTED;
            }
            return super.getPopState();
        }

        @Override
        public Icon getIcon() {
            if (isEnabled() && isSelected()) {
                Icon selectedIcon = myPresentation.getSelectedIcon();
                if (selectedIcon != null) return selectedIcon;
            }
            return super.getIcon();
        }
    }
}
