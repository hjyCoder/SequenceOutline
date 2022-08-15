package com.muy.view.window.sequence.tree;

import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.intellij.util.concurrency.NonUrgentExecutor;
import com.muy.common.tree.MTTreeCell;
import com.muy.common.tree.TreeCellEventExeContext;
import com.muy.common.tree.TreeCellEventHandle;
import com.muy.common.tree.TreePanelMark;
import com.muy.common.tree.actions.*;
import com.muy.common.tree.treeinvoke.TreeInvokeTreeCell;
import com.muy.common.utils.JacksonUtils;
import com.muy.domain.bean.invoketree.TreeInvokeModel;
import com.muy.service.SequenceGenerator;
import com.muy.service.SequenceParams;
import com.muy.service.filters.config.FilterConfig;
import com.muy.utils.*;
import com.muy.view.component.TabContentRightShow;
import com.muy.view.component.SequenceOutlineComponent;
import com.muy.view.window.sequence.action.GenPlantUmlAction;
import com.muy.view.window.sequence.bean.TreeNodeModelSequence;
import com.muy.view.window.sequence.configuration.SequenceConfiguration;
import com.muy.view.window.sequence.view.SequenceEntrancePanel;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * @Author jiyanghuang
 * @Date 2022/8/5 00:17
 */
public class MTTreeCellSequenceEntrance implements MTTreeCell {

    private Project project;

    @Getter
    private TreeNodeModelSequence treeNodeModelSequence;

    private SequenceEntrancePanel sequenceEntrancePanel;

    public MTTreeCellSequenceEntrance(Project project, TreeNodeModelSequence treeNodeModelSequence) {
        this.project = project;
        this.treeNodeModelSequence = treeNodeModelSequence;
    }

    @Override
    public Project project() {
        return project;
    }

    @Override
    public String cellShow() {
        return treeNodeModelSequence.getClassName() + "." + treeNodeModelSequence.getMethodName();
    }

    @Override
    public Icon iconSelected() {
        return AllIcons.Nodes.Lambda;
    }

    @Override
    public Icon iconUnselected() {
        return AllIcons.Nodes.Lambda;
    }

    @Override
    public void treeSelectionListener(JTree tree, DefaultMutableTreeNode mutableTreeNode, TreePanelMark treePanelMark) {
        try {
            if (null == sequenceEntrancePanel) {
                if (null == treeNodeModelSequence.getFilterConfig()) {
                    treeNodeModelSequence.setFilterConfig(new FilterConfig());
                }
                if(null == treeNodeModelSequence.getFilterConfigShow()){
                    treeNodeModelSequence.setFilterConfigShow(new FilterConfig());
                }
                sequenceEntrancePanel = new SequenceEntrancePanel(project, treeNodeModelSequence);
            }
            TabContentRightShow tabContentRightShow = SequenceOutlineComponent.getInstance(project).findInstance(TabContentRightShow.class);
            tabContentRightShow.updatePanel(sequenceEntrancePanel);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void keyEnter(JTree tree, DefaultMutableTreeNode mutableTreeNode, TreePanelMark treePanelMark) {
        if (null == treeNodeModelSequence) {
            return;
        }
        JTreeUtils.processLoading(TreeCellEventExeContext.of(tree, mutableTreeNode), new TreeCellEventHandle<Void>() {

            @Override
            public void process(TreeCellEventExeContext exeContext, CompletableFuture<Void> cf) throws Exception {
                ReadAction.nonBlocking(() -> {
                    final PsiMethod psiMethod = SequenceOutlinePsiUtils.findPsiMethodSign(GoToSourceUtils.psiManager(project), treeNodeModelSequence.fClassName(), treeNodeModelSequence.getMethodName(), treeNodeModelSequence.getMethodSignature());
                    if (null == psiMethod) {
                        return null;
                    }
                    SequenceParams paramsScan = SequenceParams.convertToSequenceParams(treeNodeModelSequence.getFilterConfig(), psiMethod);
                    SequenceGenerator sequenceGenerator = new SequenceGenerator(paramsScan);
                    SequenceParams paramsShow = SequenceParams.convertToSequenceParams(treeNodeModelSequence.getFilterConfigShow(), psiMethod);
                    sequenceGenerator.setParamsStack(paramsShow);
                    sequenceGenerator.generate(psiMethod);
                    TreeInvokeModel root = sequenceGenerator.getRoot();
                    if(null != root){
                        treeNodeModelSequence.setRoot(root);
                        Pair<TreeInvokeModel, TreeInvokeTreeCell> treeRoot = TreeUtils.treeConvert(root, MTTreeCellSequenceEntrance.this::convertTo, TreeInvokeModel::getSubInvoke, (r, s) -> r.parentChildHandle(r, s));
                        return treeRoot.getRight();
                    }
                    return null;
                }).finishOnUiThread(ModalityState.defaultModalityState(), p -> {
                    if(null != p){
                        List<MTTreeCell> showList = Lists.newArrayList(p);
                        reload(tree, mutableTreeNode, showList);
                    }
                    cf.complete(null);
                }).inSmartMode(project).submit(NonUrgentExecutor.getInstance());
            }
        });
    }

    @Override
    public ActionGroup rightClickActionGroup(JTree tree, DefaultMutableTreeNode mutableTreeNode, DefaultActionGroup defaultActionGroup, TreePanelMark treePanelMark) {
        defaultActionGroup.add(new SaveRightContentLocalAction(treePanelMark));
        defaultActionGroup.add(new SaveRightContentAction(treePanelMark));
        defaultActionGroup.add(new TreeDeleteLocalAction(treePanelMark));
        defaultActionGroup.add(new ReloadSubLocalAction(treePanelMark));
        defaultActionGroup.add(new GenPlantUmlAction(treePanelMark));
        defaultActionGroup.add(new DeleteAllChildTreeNodeAction(treePanelMark));
        defaultActionGroup.add(new DeleteCurrentTreeNodeAction(treePanelMark));
        defaultActionGroup.add(new DeleteCurrentAndChildTreeNodeAction(treePanelMark));
        defaultActionGroup.add(new CopyRightContentAction(treePanelMark));
        defaultActionGroup.add(new PasteRightContentAction(treePanelMark));
        return defaultActionGroup;
    }

    @Override
    public void saveRightContentLocal() {
        if(null == sequenceEntrancePanel || null == sequenceEntrancePanel.getTreeNodeModelSequence()){
            return;
        }
        sequenceEntrancePanel.saveConfig();
        SequenceConfiguration sequenceConfiguration = SequenceConfiguration.getInstance(project);
        sequenceConfiguration.addData(treeNodeModelSequence);
        sequenceEntrancePanel.getTabJsonWrap().fillSource(JacksonUtils.toJSONString(treeNodeModelSequence));
    }

    @Override
    public void treeDeleteLocal(TreePanelMark treePanelMark, DefaultMutableTreeNode mutableTreeNode) {
        SequenceConfiguration sequenceConfiguration = SequenceConfiguration.getInstance(project);
        sequenceConfiguration.removeData(treeNodeModelSequence);
        removeCurrent(treePanelMark.jTree(), mutableTreeNode);
    }

    @Override
    public void reloadSubLocal(TreePanelMark treePanelMark, DefaultMutableTreeNode mutableTreeNode) {
        TreeInvokeModel root = treeNodeModelSequence.getRoot();
        if(null == root){
            return;
        }
        Pair<TreeInvokeModel, TreeInvokeTreeCell> treeRoot = TreeUtils.treeConvert(root, MTTreeCellSequenceEntrance.this::convertTo, TreeInvokeModel::getSubInvoke, (r, s) -> r.parentChildHandle(r, s));
        reload(treePanelMark.jTree(), mutableTreeNode, Lists.newArrayList(treeRoot.getRight()));
    }

    @Override
    public void copyRightContent(TreePanelMark treePanelMark, DefaultMutableTreeNode mutableTreeNode) {
        TreeNodeModelSequence copy = treeNodeModelSequence.copyConfig();
        ClipboardUtils.fillStringToClip(JacksonUtils.toJSONString(copy));
    }

    @Override
    public void pasteRightContent(TreePanelMark treePanelMark, DefaultMutableTreeNode mutableTreeNode) {
        String json = ClipboardUtils.fetchStringFromClip();
        TreeNodeModelSequence jsonConvert = JacksonUtils.toJavaObject(json, TreeNodeModelSequence.class);
        if (TreeNodeModelSequence.configValid(jsonConvert)) {
            FilterConfig.updateField(jsonConvert.getFilterConfig(), treeNodeModelSequence.getFilterConfig());
            FilterConfig.updateField(jsonConvert.getFilterConfigShow(), treeNodeModelSequence.getFilterConfigShow());
            sequenceEntrancePanel.updateConfigUI();
        } else {
            throw new RuntimeException("Invalid Json");
        }
    }

    private TreeInvokeTreeCell convertTo(TreeInvokeModel treeInvokeModel){
        if(null == treeInvokeModel || null == treeInvokeModel.getTreeNodeModel()){
            return null;
        }
        TreeInvokeTreeCell treeInvokeTreeCell = new TreeInvokeTreeCell(project, treeInvokeModel.getTreeNodeModel(), treeInvokeModel);
        return treeInvokeTreeCell;
    }
}
