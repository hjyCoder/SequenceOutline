package com.muy.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.muy.common.bean.BeanInvokeParam;
import com.muy.common.bean.BeanInvokeType;
import com.muy.common.exception.SequenceOutlineException;
import com.muy.common.notification.SequenceOutlineNotifier;
import com.muy.utils.ClipboardUtils;
import com.muy.utils.JacksonUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @Author jiyanghuang
 * @Date 2022/5/26 00:57
 */
public class ConstructorInvokeParamAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        PsiElement psiElement = event.getData(CommonDataKeys.PSI_ELEMENT);
        if(!(psiElement instanceof PsiMethod)){
            return;
        }
        PsiMethod psiMethod = (PsiMethod)psiElement;

        try {
            BeanInvokeParam beanInvokeParam = BeanInvokeParam.ofInvokeMethod(psiMethod, BeanInvokeType.CONSTRUCT_INVOKE_METHOD);
            ClipboardUtils.fillStringToClip(JacksonUtils.toJSONString(beanInvokeParam));
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
