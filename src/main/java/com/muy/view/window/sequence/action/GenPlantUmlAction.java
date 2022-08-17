package com.muy.view.window.sequence.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.testFramework.LightVirtualFile;
import com.muy.common.tree.MTTreeCell;
import com.muy.common.tree.TreePanelMark;
import com.muy.common.tree.actions.AbstractMTTreeCellSelectTreeAction;
import com.muy.domain.bean.invoketree.TreeInvokeModel;
import com.muy.utils.GuiUtils;
import com.muy.utils.PlantUmlSequenceCreator;
import com.muy.view.window.sequence.tree.MTTreeCellSequenceEntrance;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class GenPlantUmlAction extends AbstractMTTreeCellSelectTreeAction {

    Logger LOGGER = Logger.getInstance(GenPlantUmlAction.class);

    public GenPlantUmlAction(TreePanelMark treePanelMark) {
        super("GenPlantUml", "GenPlantUml", GuiUtils.PLANT_UML, treePanelMark);
    }

    @Override
    public void handleSelectTree(DefaultMutableTreeNode mutableTreeNode, MTTreeCell mtTreeCell) {
        if(!(mtTreeCell instanceof MTTreeCellSequenceEntrance)){
            return;
        }
        MTTreeCellSequenceEntrance entrance = (MTTreeCellSequenceEntrance)mtTreeCell;
        if(null == entrance.getTreeNodeModelSequence() || null == entrance.getTreeNodeModelSequence().getRoot()){
            return;
        }
        TreeInvokeModel treeInvokeModel = entrance.getTreeNodeModelSequence().getRoot();
        TreeInvokeModel rootForShow = TreeInvokeModel.ofRoot();
        rootForShow.getSubInvoke().add(treeInvokeModel);
        PlantUmlSequenceCreator plantUmlSequenceCreator = new PlantUmlSequenceCreator<TreeInvokeModel>(rootForShow, TreeInvokeModel::getSubInvoke, TreeInvokeModel::fClassName, TreeInvokeModel::className, TreeInvokeModel::nodeValue, TreeInvokeModel::reValue, t -> true);
        try {
            String pumlContent = plantUmlSequenceCreator.plantUmlTextSkip();
            LightVirtualFile sourceVirtualFile = new LightVirtualFile("temp.puml", pumlContent);
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(mtTreeCell.project());
            fileEditorManager.openFile(sourceVirtualFile, true);
            StringSelection selection = new StringSelection(pumlContent);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        } catch (Exception ex) {
            LOGGER.error(String.format("GenPlantUml error"), ex);
        }
    }
}
