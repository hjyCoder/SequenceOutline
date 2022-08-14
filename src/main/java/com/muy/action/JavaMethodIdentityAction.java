package com.muy.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.muy.common.exception.SequenceOutlineException;
import com.muy.common.notification.SequenceOutlineNotifier;
import com.muy.domain.bean.invoketree.TreeNodeModel;
import org.jetbrains.annotations.NotNull;

/**
 * @Author jiyanghuang
 * @Date 2022/7/4 23:30
 */
public class JavaMethodIdentityAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        PsiElement psiElement = event.getData(CommonDataKeys.PSI_ELEMENT);
        if(!(psiElement instanceof PsiMethod)){
            return;
        }
        PsiMethod psiMethod = (PsiMethod)psiElement;
//        RealizationVisitor realizationVisitor = new RealizationVisitor();
//        realizationVisitor.methodAccept(psiMethod);

        try {
            TreeNodeModel.of(psiMethod);
        } catch (SequenceOutlineException sequenceOutlineException) {
            if (null != sequenceOutlineException.getResponseCode()) {
                SequenceOutlineNotifier.notify(sequenceOutlineException.getResponseCode().getDesc());
                return;
            }
            SequenceOutlineNotifier.notify("error");
        } catch (Exception ex) {
            SequenceOutlineNotifier.notify(ex.getMessage());
        }
    }
}
