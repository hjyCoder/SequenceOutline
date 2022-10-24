package com.muy.action;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.muy.common.bean.BeanInvokeParam;
import com.muy.common.bean.BeanInvokeType;
import com.muy.common.dialog.DialogFormConstructorSelect;
import com.muy.common.dialog.MRDialog;
import com.muy.common.exception.SequenceOutlineException;
import com.muy.common.notification.SequenceOutlineNotifier;
import com.muy.utils.ClipboardUtils;
import com.muy.utils.JacksonUtils;
import com.muy.utils.ReflectStringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @Author jiyanghuang
 * @Date 2022/5/26 00:57
 */
public class BeanInvokeParamAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        PsiElement psiElement = event.getData(CommonDataKeys.PSI_ELEMENT);

        PsiMethod[] psiMethodInvoke = {null};
        String fulClassName = "";
        if(psiElement instanceof PsiClass){
            PsiClass psiClass = (PsiClass)psiElement;
            fulClassName = psiClass.getQualifiedName();
            PsiMethod[] allMethods = psiClass.getAllMethods();
            Map<String, PsiMethod> psiMethodMap = Maps.newTreeMap();
            for(PsiMethod psiM : allMethods){
                String methodDesc = ReflectStringUtils.methodNameDesc(psiM);
                if ("Object()".equals(methodDesc)) {
                    continue;
                }
                psiMethodMap.put(methodDesc, psiM);
            }
            DialogFormConstructorSelect dialogFormConstructorSelect = new DialogFormConstructorSelect(Lists.newArrayList(psiMethodMap.keySet()), (methodSign) -> {
                psiMethodInvoke[0] = psiMethodMap.get(methodSign);
            });
            MRDialog.of(dialogFormConstructorSelect).show();

        }else if(psiElement instanceof PsiMethod){
            psiMethodInvoke[0] = (PsiMethod)psiElement;
        }else{
            return;
        }
        if(null == psiMethodInvoke[0]){
            return;
        }

        try {
            BeanInvokeParam beanInvokeParam = BeanInvokeParam.ofInvokeMethod(psiMethodInvoke[0], fulClassName, BeanInvokeType.ONLY_METHOD);
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
