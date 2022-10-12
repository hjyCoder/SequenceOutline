package com.muy.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.muy.common.bean.BeanFindParam;
import com.muy.common.exception.SequenceOutlineException;
import com.muy.common.notification.SequenceOutlineNotifier;
import com.muy.utils.ClipboardUtils;
import com.muy.utils.JacksonUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @Author jiyanghuang
 * @Date 2022/5/26 00:57
 */
public class BeanFindParamAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        PsiElement psiElement = event.getData(CommonDataKeys.PSI_ELEMENT);
        if (!(psiElement instanceof PsiField)) {
            return;
        }
        PsiField psiField = (PsiField) psiElement;

        try {
            BeanFindParam beanFindParam = BeanFindParam.of(psiField);
            StringBuilder sb = new StringBuilder();
            sb.append("\"beanFind\":" + JacksonUtils.toJSONString(beanFindParam));
            ClipboardUtils.fillStringToClip(sb.toString());
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
