package com.muy.view.panel.json;

import com.intellij.json.JsonBundle;
import com.intellij.json.psi.JsonFile;
import com.intellij.jsonpath.ui.JsonPathEvaluateManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ThrowableRunnable;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import com.intellij.util.ui.update.MergingUpdateQueue;
import com.intellij.util.ui.update.Update;
import com.muy.common.actions.*;
import com.muy.common.utils.JsonUtils;
import com.muy.utils.ActionUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.FocusManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class JsonPathEvaluateSnippetViewJava extends JsonPathEvaluateView{

    private Project project;

    private MergingUpdateQueue expressionHighlightingQueue = new MergingUpdateQueue("JSONPATH_EVALUATE", 1000, true, null, this);
    private Editor sourceEditor = initJsonEditor("source.json", false, EditorKind.CONSOLE);

    private DefaultActionGroup rightClickActionGroup;

    private ActionPopupMenu actionPopupMenu;

    public JsonPathEvaluateSnippetViewJava(Project project){
        super(project);
        this.project = project;

        BorderLayoutPanel sourcePanel = new BorderLayoutPanel();
        sourcePanel.addToTop(searchWrapper);

        BorderLayoutPanel sourceWrapper = new BorderLayoutPanel();
        JBLabel sourceLabel = new JBLabel(JsonBundle.message("jsonpath.evaluate.input"));
        sourceLabel.setBorder(JBUI.Borders.empty(3, 6));
        sourceWrapper.addToTop(sourceLabel);
        sourceWrapper.addToCenter(sourceEditor.getComponent());

        sourcePanel.addToCenter(sourceWrapper);

        OnePixelSplitter splitter = new OnePixelSplitter(0.99f);
        splitter.setFirstComponent(sourcePanel);
        splitter.setSecondComponent(resultWrapper);

        setContent(splitter);
        setSource("{\n\n}");

        sourceEditor.getComponent().setBorder(JBUI.Borders.customLine(JBColor.border(), 1, 0, 0, 0));
        sourceEditor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                expressionHighlightingQueue.queue(Update.create(JsonPathEvaluateSnippetViewJava.this, new Runnable() {
                    @Override
                    public void run() {
                        resetExpressionHighlighting();
                    }
                }));
            }
        });

        MessageBusConnection messageBusConnection = this.project.getMessageBus().connect(this);
        messageBusConnection.subscribe(ToolWindowManagerListener.TOPIC, new ToolWindowManagerListener() {
            @Override
            public void stateChanged(@NotNull ToolWindowManager toolWindowManager) {
                ToolWindow toolWindow = toolWindowManager.getToolWindow(JsonPathEvaluateManager.EVALUATE_TOOLWINDOW_ID);
                if(null != toolWindow){
                    splitter.setOrientation(!toolWindow.getAnchor().isHorizontal());
                }
            }
        });

        initToolbar();

        rightClickActionGroup = new DefaultActionGroup();
        rightClickActionGroup.add(new FormatJsonViewAction(this));
        rightClickActionGroup.add(new MinifyJsonViewAction(this));
        rightClickActionGroup.add(new SortJsonViewAction(this));
        rightClickActionGroup.add(new VerifyJsonViewAction(this));
        rightClickActionGroup.add(new SortIncludeListJsonViewAction(this));
        actionPopupMenu = ActionUtil.createActionPopupMenu(this, "", rightClickActionGroup);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)){
                    actionPopupMenu.getComponent().show(JsonPathEvaluateSnippetViewJava.this, e.getX(), e.getY());
                }
            }
        });
    }

    public void setSource(String json) {
        if(StringUtils.isBlank(json)){
            json = "{}";
        }
        try {
            String finalJson = json;
            WriteAction.run(new ThrowableRunnable<Throwable>() {
                @Override
                public void run() throws Throwable {
                    sourceEditor.getDocument().setText(JsonUtils.formatJsonWrap(finalJson));
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    protected JsonFile getJsonFile() {
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(sourceEditor.getDocument());
        if(null != psiFile && psiFile instanceof JsonFile){
            return (JsonFile)psiFile;
        }
        return null;
    }

    @Override
    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
        if (pressed && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            Component focusOwner = FocusManager.getCurrentManager().getFocusOwner();

            if (SwingUtilities.isDescendingFrom(focusOwner, sourceEditor.getComponent())) {
                searchTextField.requestFocus();
                return true;
            }
        }
        return super.processKeyBinding(ks, e, condition, pressed);
    }

    @Override
    public void dispose() {
        super.dispose();
        EditorFactory.getInstance().releaseEditor(sourceEditor);
    }

    public Editor getSourceEditor() {
        return sourceEditor;
    }

    public String text(){
        return this.getSourceEditor().getDocument().getText();
    }

    public DefaultActionGroup rightClickActionGroup(){
        return rightClickActionGroup;
    }
}
