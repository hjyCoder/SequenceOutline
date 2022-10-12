package com.muy.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.muy.common.exception.SequenceOutlineException;
import com.muy.common.notification.SequenceOutlineNotifier;
import com.muy.utils.ClipboardUtils;
import com.muy.utils.JacksonUtils;
import com.muy.utils.PsiTypeToKvForJsonUtils;

import java.util.Map;

public class POJO2JsonAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        Project project = e.getProject();
        PsiElement elementAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass selectedClass = PsiTreeUtil.getContextOfType(elementAt, PsiClass.class);
        try {
            Map<String, Object> kv = PsiTypeToKvForJsonUtils.getFields(selectedClass);
            ClipboardUtils.fillStringToClip(JacksonUtils.toJSONString(kv));
            String message = "Convert " + selectedClass.getName() + " to JSON success, copied to clipboard.";
            SequenceOutlineNotifier.notify(message);
        } catch (SequenceOutlineException ex) {
            SequenceOutlineNotifier.notifyError(ex.getMessage());
        } catch (Exception ex) {
            SequenceOutlineNotifier.notifyError(ex.getMessage());
        }
    }
}


