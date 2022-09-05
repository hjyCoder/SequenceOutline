package com.muy.action;

import com.alibaba.fastjson.JSON;
import com.intellij.jsonpath.ui.JsonPathEvaluateManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.muy.common.exception.SequenceOutlineException;
import com.muy.common.notification.SequenceOutlineNotifier;
import com.muy.domain.bean.invoketree.TreeNodeModel;
import com.muy.utils.ClipboardUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

/**
 * @Author jiyanghuang
 * @Date 2022/7/4 23:30
 */
public class JavaMethodIdentityAction extends AnAction {

    Logger LOGGER = Logger.getInstance(JavaMethodIdentityAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        JsonPathEvaluateManager jsonPathEvaluateManager = JsonPathEvaluateManager.getInstance(event.getProject());
        Field[] fields = jsonPathEvaluateManager.getClass().getFields();

        PsiElement psiElement = event.getData(CommonDataKeys.PSI_ELEMENT);
        if (!(psiElement instanceof PsiMethod)) {
            return;
        }
        PsiMethod psiMethod = (PsiMethod) psiElement;

        try {
            TreeNodeModel treeNodeModel = TreeNodeModel.of(psiMethod);
            ClipboardUtils.fillStringToClip(JSON.toJSONString(treeNodeModel));
            SequenceOutlineNotifier.notify("Copy JavaMethodIdentity Success");
        } catch (SequenceOutlineException sequenceOutlineException) {
            if (null != sequenceOutlineException.getResponseCode()) {
                SequenceOutlineNotifier.notifyError(sequenceOutlineException.getResponseCode().getDesc());
                return;
            }
            SequenceOutlineNotifier.notifyError("error");
        } catch (Exception ex) {
            SequenceOutlineNotifier.notify(ex.getMessage());
            LOGGER.error(String.format("JavaMethodIdentityAction error"), ex);
        }
    }
}
