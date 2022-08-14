package com.muy.common.textfield;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.util.concurrency.NonUrgentExecutor;
import com.muy.common.notification.SequenceOutlineNotifier;
import com.muy.common.tree.MTTreeCell;
import com.muy.common.tree.TreePanelMark;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.function.Consumer;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 22:00
 */
public class SearchKeywordFunAction extends AnAction implements CustomComponentAction, DumbAware {

    private Consumer<String> search;

    private Project project;

    private MRSearchTextField searchTextField;

    protected TreePanelMark treePanelMark;

    public SearchKeywordFunAction(TreePanelMark treePanelMark){
        super("searchKeyword", "输入回车", AllIcons.Actions.Search);
        this.treePanelMark = treePanelMark;
        this.search = search;
        searchTextField = new MRSearchTextField(this::perform);
        searchTextField.getTextEditor().setColumns(20);
//        searchTextField.setColumns(20);

//        tfSearch.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//                if(e.getKeyCode() == KeyEvent.VK_ENTER){
//                    e.consume();
//                    perform();
//                }
//            }
//        });
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        perform(searchTextField.getText());
    }

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        return searchTextField;
    }

    private boolean perform(String text) {
        try {
            DefaultMutableTreeNode mutableTreeNode = (DefaultMutableTreeNode) treePanelMark.jTree().getLastSelectedPathComponent();
            if (null == mutableTreeNode) {
                mutableTreeNode = treePanelMark.rootTreeNode();
            }
            final DefaultMutableTreeNode mutableTreeNodeT = mutableTreeNode;
            if (mutableTreeNodeT.getUserObject() instanceof MTTreeCell) {
                ReadAction.nonBlocking(() -> {
                    MTTreeCell mtTreeCell = (MTTreeCell) mutableTreeNodeT.getUserObject();
                    mtTreeCell.searchKeyword(treePanelMark, mutableTreeNodeT, text);
                    return null;
                }).finishOnUiThread(ModalityState.defaultModalityState(), p -> {

                }).inSmartMode(project).submit(NonUrgentExecutor.getInstance());
            } else {
                SequenceOutlineNotifier.notify("Invalid treeNodeSelect");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
}
