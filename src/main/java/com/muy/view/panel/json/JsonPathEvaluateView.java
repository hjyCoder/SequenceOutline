package com.muy.view.panel.json;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.find.FindBundle;
import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.json.JsonBundle;
import com.intellij.json.json5.Json5FileType;
import com.intellij.json.psi.JsonFile;
import com.intellij.jsonpath.JsonPathFileType;
import com.intellij.jsonpath.ui.JsonPathEvaluateManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ActionButtonLook;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.NlsActions;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.serialization.ClassUtil;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanelWithEmptyText;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.popup.PopupState;
import com.intellij.util.ThrowableRunnable;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.muy.utils.FieldReflectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.*;
import java.util.function.Supplier;


/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public abstract class JsonPathEvaluateView extends SimpleToolWindowPanel implements Disposable {

    private Project project;
    protected EditorTextField searchTextField;
    protected JPanel searchWrapper;

    protected JBPanelWithEmptyText resultWrapper;

    private JBLabel resultLabel;
    private Editor resultEditor;

    private JBTextArea errorOutputArea;
    private JScrollPane errorOutputContainer;
    private Set<Option> evalOptions = new HashSet<>();

    public JsonPathEvaluateView(Project project){
        super(true, true);
        this.project = project;
        init();
    }

    private void init(){
        searchTextField = new EditorTextField(project, JsonPathFileType.INSTANCE){
            @Override
            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && pressed) {
                    evaluate();
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
                    // com.intellij.jsonpath.JsonPathCompletionContributor.JsonKeysCompletionProvider.addCompletions 被使用的地方
                    // JsonPathEvaluateManager.JSON_PATH_EVALUATE_SOURCE_KEY 这个key很重要，与后面提示获取json文件强关联
                    FieldReflectUtils.findKey(project, FieldReflectUtils.JSON_PATH_EVALUATE_EXPRESSION_KEY, (k) -> {
                        psiFile.putUserData(k, true);
                    });

                    FieldReflectUtils.findKey(project, FieldReflectUtils.JSON_PATH_EVALUATE_SOURCE_KEY, (k) -> {
                        psiFile.putUserData((Key<Supplier>) k, () -> getJsonFile());
                    });
                }
                return editor;
            }
        };

        searchWrapper = new NonOpaquePanel(){
            @Override
            public void updateUI() {
                super.updateUI();
                this.setBackground(UIUtil.getTextFieldBackground());
            }
        };

        resultWrapper = new JBPanelWithEmptyText(new BorderLayout());
        resultLabel = new JBLabel(JsonBundle.message("jsonpath.evaluate.result"));
        resultEditor = initJsonEditor("result.json", true, EditorKind.PREVIEW);

        FieldReflectUtils.findKey(project, FieldReflectUtils.JSON_PATH_EVALUATE_RESULT_KEY, (k) -> {
            resultEditor.putUserData(k, true);
        });
        resultEditor.setBorder(JBUI.Borders.customLine(JBColor.border(), 1, 0, 0, 0));
        resultLabel.setBorder(JBUI.Borders.empty(3, 6));
        resultWrapper.getEmptyText().setText(JsonBundle.message("jsonpath.evaluate.no.result"));

        errorOutputArea = new JBTextArea();
        errorOutputContainer = new JBScrollPane(errorOutputArea);


        errorOutputContainer.setBorder(JBUI.Borders.customLine(JBColor.border(), 1, 0, 0, 0));

        SearchHistoryButton historyButton = new SearchHistoryButton(new ShowHistoryAction(), false);
        JPanel historyButtonWrapper = new NonOpaquePanel(new BorderLayout());
        historyButtonWrapper.setBorder(JBUI.Borders.empty(3, 6, 3, 6));
        historyButtonWrapper.add(historyButton, BorderLayout.NORTH);

        searchTextField.setFontInheritedFromLAF(false);

        searchWrapper.add(historyButtonWrapper, BorderLayout.WEST);
        searchWrapper.add(searchTextField, BorderLayout.CENTER);
        searchWrapper.setBorder(JBUI.Borders.customLine(JBColor.border(), 0, 0, 1, 0));
        searchWrapper.setOpaque(true);

        errorOutputArea.setEditable(false);
        errorOutputArea.setWrapStyleWord(true);
        errorOutputArea.setLineWrap(true);
        errorOutputArea.setBorder(JBUI.Borders.empty(10));

        setExpression("$..*");
    }

    protected void initToolbar() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        fillToolbarOptions(actionGroup);

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("toolbar", actionGroup, true);
        toolbar.setTargetComponent(this);

        setToolbar(toolbar.getComponent());
    }

    protected abstract JsonFile getJsonFile();

    protected void resetExpressionHighlighting() {
        PsiFile jsonPathFile = PsiDocumentManager.getInstance(project).getPsiFile(searchTextField.getDocument());
        if (jsonPathFile != null) {
            // reset inspections in expression
            DaemonCodeAnalyzer.getInstance(project).restart(jsonPathFile);
        }
    }

    private void fillToolbarOptions(DefaultActionGroup group) {
        ComboBoxAction outputComboBox = new ComboBoxAction() {
            @Override
            protected @NotNull
            DefaultActionGroup createPopupActionGroup(JComponent button) {
                DefaultActionGroup outputItems = new DefaultActionGroup();
                outputItems.add(new OutputOptionAction(false, JsonBundle.message("jsonpath.evaluate.output.values")));
                outputItems.add(new OutputOptionAction(true, JsonBundle.message("jsonpath.evaluate.output.paths")));
                return outputItems;
            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                Presentation presentation = e.getPresentation();
                if (null == e.getProject()) {
                    return;
                }
                if (evalOptions.contains(Option.AS_PATH_LIST)) {
                    presentation.setText(JsonBundle.message("jsonpath.evaluate.output.paths"));
                } else {
                    presentation.setText(JsonBundle.message("jsonpath.evaluate.output.values"));
                }
            }

            @Override
            public @NotNull
            JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
                JPanel jPanel = new JPanel(new GridBagLayout());
                jPanel.add(new JLabel(JsonBundle.message("jsonpath.evaluate.output.option")), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, JBUI.insetsLeft(5), 0, 0));
                jPanel.add(super.createCustomComponent(presentation, place), new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, JBUI.emptyInsets(), 0, 0));
                return jPanel;
            }
        };
        group.add(outputComboBox);
        DefaultActionGroup defaultActionGroup = new DefaultActionGroup(JsonBundle.message("jsonpath.evaluate.options"), true);
        defaultActionGroup.getTemplatePresentation().setIcon(AllIcons.General.Settings);
        defaultActionGroup.add(new OptionToggleAction(Option.SUPPRESS_EXCEPTIONS, JsonBundle.message("jsonpath.evaluate.suppress.exceptions")));
        defaultActionGroup.add(new OptionToggleAction(Option.ALWAYS_RETURN_LIST, JsonBundle.message("jsonpath.evaluate.return.list")));
        defaultActionGroup.add(new OptionToggleAction(Option.DEFAULT_PATH_LEAF_TO_NULL, JsonBundle.message("jsonpath.evaluate.nullize.missing.leaf")));
        defaultActionGroup.add(new OptionToggleAction(Option.REQUIRE_PROPERTIES, JsonBundle.message("jsonpath.evaluate.require.all.properties")));
        group.add(defaultActionGroup);
    }

    protected Editor initJsonEditor(String fileName, Boolean isViewer, EditorKind kind) {
        LightVirtualFile sourceVirtualFile = new LightVirtualFile(fileName, Json5FileType.INSTANCE, "");
        PsiFile sourceFile = PsiManager.getInstance(project).findFile(sourceVirtualFile);
        Document document = PsiDocumentManager.getInstance(project).getDocument(sourceFile);
        Editor editor = EditorFactory.getInstance().createEditor(document, project, sourceVirtualFile, isViewer, kind);
        editor.getSettings().setLineNumbersShown(false);
        return editor;
    }

    void setExpression(String jsonPathExpr) {
        searchTextField.setText(jsonPathExpr);
    }

    private void setResult(String result) {
        try {
            WriteAction.run(new ThrowableRunnable<Throwable>() {
                @Override
                public void run() throws Throwable {

                    resultEditor.getDocument().setText(result);
                    PsiDocumentManager.getInstance(project).commitDocument(resultEditor.getDocument());
                    PsiFile  psiFile = PsiDocumentManager.getInstance(project).getPsiFile(resultEditor.getDocument());
                    if(null != psiFile){
                        new ReformatCodeProcessor(psiFile, false).run();
                    }

                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        if (!Lists.newArrayList(resultWrapper.getComponents()).contains(resultEditor.getComponent())) {
            resultWrapper.removeAll();
            resultWrapper.add(resultLabel, BorderLayout.NORTH);

            resultWrapper.add(resultEditor.getComponent(), BorderLayout.CENTER);
            resultWrapper.revalidate();
            resultWrapper.repaint();
        }

        resultEditor.getCaretModel().moveToOffset(0);
    }

    private void setError(String error) {
        errorOutputArea.setText(error);

        if (!Lists.newArrayList(resultWrapper.getComponents()).contains(errorOutputArea)) {
            resultWrapper.removeAll();
            resultWrapper.add(resultLabel, BorderLayout.NORTH);

            resultWrapper.add(errorOutputContainer, BorderLayout.CENTER);
            resultWrapper.revalidate();
            resultWrapper.repaint();
        }
    }

    private void evaluate(){
        String expression = searchTextField.getText();
        if(StringUtils.isBlank(expression)){
            return;
        }
        JsonPath jsonPath;
        try{
            jsonPath = JsonPath.compile(expression);
        }catch (Exception ex){
            setError(ex.getLocalizedMessage());
            return;
        }
        addJSONPathToHistory(expression);

        Configuration config = new Configuration.ConfigurationBuilder()
                .options(evalOptions)
                .build();

        String json = getJsonFile().getText();
        if (StringUtils.isBlank(json)) {
            setError(JsonBundle.message("jsonpath.evaluate.file.not.found"));
            return;
        }
        DocumentContext jsonDocument;
        try{
            jsonDocument = JsonPath.parse(json, config);
        }catch (Exception ex){
            setError(ex.getLocalizedMessage());
            return;
        }
        Object result;
        try{
             result = jsonDocument.read(jsonPath);
        }catch (Exception ex){
            setError(ex.getLocalizedMessage());
            return;
        }
        setResult(toResultString(config, result));
    }

    private String toResultString(Configuration config, Object result)  {
        if (result == null) {
            return "null";
        }
        if (result instanceof String) {
            return "\"" + StringUtil.escapeStringCharacters((String)result) + "\"";
        }

        if (ClassUtil.isPrimitive(result.getClass())) {
            return result.toString();
        }

        if (result instanceof Collection) {
            // .keys() result is Set<String>
            Collection collection = (Collection)result;
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            collection.forEach(e -> {
                sb.append(toResultString(config, e));
                sb.append(", ");
            });
            sb.delete(sb.length() - ", ".length(), sb.length());
            sb.append("]");
            return sb.toString();
        }

        return config.jsonProvider().toJson(result);
    }


    @Override
    public void dispose(){
        EditorFactory.getInstance().releaseEditor(resultEditor);
    }

    private class OutputOptionAction extends DumbAwareAction{

        private Boolean enablePaths;

        public OutputOptionAction(Boolean enablePaths, @NlsActions.ActionText String message){
            super(message);
            this.enablePaths = enablePaths;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            if (enablePaths) {
                evalOptions.add(Option.AS_PATH_LIST);
            }
            else {
                evalOptions.remove(Option.AS_PATH_LIST);
            }
            evaluate();
        }
    }

    private class OptionToggleAction extends ToggleAction{

        private Option option;
        public OptionToggleAction(Option option, @NlsActions.ActionText String message){
            super(message);
            this.option = option;
        }
        @Override
        public boolean isSelected(@NotNull AnActionEvent e) {
            return evalOptions.contains(option);
        }

        @Override
        public void setSelected(@NotNull AnActionEvent e, boolean state) {
            if (state) {
                evalOptions.add(option);
            }
            else {
                evalOptions.remove(option);
            }
            evaluate();
        }
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

    /**
     *
     * @return
     */
    private List<String> getExpressionHistory() {
        String split = PropertiesComponent.getInstance().getValue("JSONPathEvaluateHistory");
        if(StringUtils.isEmpty(split)){
            return Lists.newArrayList();
        }
        return Lists.newArrayList(split.split("\n"));
    }

    /**
     *
     * @param history
     */
    private void setExpressionHistory(Collection<String> history) {
        PropertiesComponent.getInstance().setValue("JSONPathEvaluateHistory", String.join("\n",history));
    }

    private void addJSONPathToHistory(String path) {
        if(StringUtils.isBlank(path)){
            return;
        }

        Deque<String> history = new ArrayDeque(getExpressionHistory());
        if (!history.contains(path)) {
            history.addFirst(path);
            if (history.size() > 10) {
                history.removeLast();
            }
            setExpressionHistory(history);
        } else {
            if (history.getFirst() == path) {
                return;
            }
            history.remove(path);
            history.addFirst(path);
            setExpressionHistory(history);
        }
    }

    private class ShowHistoryAction extends DumbAwareAction {

        private PopupState<JBPopup> popupState  = PopupState.forPopup();

        public ShowHistoryAction(){
            super(FindBundle.message("find.search.history"), null, AllIcons.Actions.SearchWithHistory);
            registerCustomShortcutSet(KeymapUtil.getActiveKeymapShortcuts("ShowSearchHistory"), searchTextField);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            if(popupState.isRecentlyHidden()){
                return;
            }
            showCompletionPopup(searchWrapper, getExpressionHistory(), searchTextField, popupState);
        }

        private void showCompletionPopup(JComponent toolbarComponent,
                                         List<String> list,
                                         EditorTextField textField,
                                         PopupState<JBPopup> popupState) {
            PopupChooserBuilder<?> builder  = (PopupChooserBuilder)JBPopupFactory.getInstance().createPopupChooserBuilder(list);
            JBPopup popup = builder
                    .setMovable(false)
                    .setResizable(false)
                    .setRequestFocus(true)
                    .setItemChoosenCallback(new Runnable() {
                        @Override
                        public void run() {
                            PopupChooserBuilder.PopupComponentAdapter popupListAdapter = (PopupChooserBuilder.PopupComponentAdapter)builder.getChooserComponent();
                            JList jList = (JList)popupListAdapter.getComponent();
                            String selectedValue = (String)jList.getSelectedValue();
                            if (selectedValue != null) {
                                textField.setText(selectedValue);
                                IdeFocusManager.getGlobalInstance().requestFocus(textField, false);
                            }
                        }
                    }).createPopup();

            popupState.prepareToShow(popup);
            if (toolbarComponent != null) {
                popup.showUnderneathOf(toolbarComponent);
            }
            else {
                popup.showUnderneathOf(textField);
            }
        }
    }
}
