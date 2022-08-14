package com.muy.action;

import com.muy.domain.bean.invoketree.TreeInvokeModel;
import com.muy.utils.ClipboardUtils;
import com.muy.utils.PlantUmlSequenceCreator;


/**
 * @Author jiyanghuang
 * @Date 2022/6/29 22:47
 */
public class SequencePlantUmlAction extends SequenceActionAbstract {

    @Override
    protected void genAfter(TreeInvokeModel root) {
        genSkip(root);
    }

    public void genSkip(TreeInvokeModel root) {

        TreeInvokeModel rootForShow = TreeInvokeModel.ofRoot();
        rootForShow.getSubInvoke().add(root);
        PlantUmlSequenceCreator plantUmlSequenceCreator = new PlantUmlSequenceCreator<TreeInvokeModel>(rootForShow, TreeInvokeModel::getSubInvoke, TreeInvokeModel::fClassName, TreeInvokeModel::className, TreeInvokeModel::nodeValue, TreeInvokeModel::reValue, t -> true);
        try {
            String pumlContent = plantUmlSequenceCreator.plantUmlTextSkip();
//            LightVirtualFile sourceVirtualFile = new LightVirtualFile("temp.puml", pumlContent);
//            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
//            fileEditorManager.openFile(sourceVirtualFile, true);
            System.out.println("----> open ---> ");
            ClipboardUtils.fillStringToClip(pumlContent);
//        fileEditorManager.setSelectedEditor(sourceVirtualFile, "");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("----> open file ex ---> " + ex.getMessage());
        }
    }
}
