package com.muy.action;

import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.actions.ReloadFileAction;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.debugger.ui.HotSwapUI;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.concurrency.NonUrgentExecutor;
import com.muy.common.bean.BeanInvokeParam;
import com.muy.common.bean.BeanInvokeType;
import com.muy.common.notification.SequenceOutlineNotifier;
import com.muy.common.utils.JacksonUtils;
import com.muy.utils.ReflectStringUtils;
import com.muy.utils.XPathParserUtils;
import com.muy.view.window.rest.bean.HttpRequestData;
import com.muy.view.window.rest.bean.Response;
import com.muy.view.window.rest.service.impl.RequestExecutorApache;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @Author jiyanghuang
 * @Date 2022/10/22 23:46
 */
public class MybatisHotswapAction extends ReloadFileAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            VirtualFile[] files = getCompilableFiles(project, e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY));
            String fulClassName = "";
            if (files.length > 0) {
                VirtualFile first = files[0];
                if(first.getPath().endsWith(".xml")){
                    // 如果是XML，则顺便查询 mapper.java 一起重新加载
                    fulClassName = XPathParserUtils.mapperNamespace(first.getPath());
                }else if(first.getPath().endsWith(".java")){
                    // 如果仅仅是 mapper.java 则只重新加载该类
                    fulClassName = ReflectStringUtils.fulClassName(first.getPath());
                }
                if(StringUtils.isBlank(fulClassName)){
                    SequenceOutlineNotifier.notifyError("invalid mybatis hotswap!");
                    return;
                }
                DebuggerSession session = DebuggerManagerEx.getInstanceEx(project).getContext().getDebuggerSession();
                if (session != null) {
                    HotSwapUI.getInstance(project).compileAndReload(session, files);
                    reloadMybatisReq(project, fulClassName);
                }
            }
        }
    }

    /**
     * 请求 agent 重新加载 Mybatis类
     * @param project
     * @param fulClassName
     */
    private void reloadMybatisReq(Project project, String fulClassName){
        HttpRequestData httpRequestData = new HttpRequestData();
        RequestExecutorApache requestExecutor = new RequestExecutorApache();
        BeanInvokeParam beanInvokeParam = new BeanInvokeParam();
        beanInvokeParam.setClassFullName(fulClassName);
        beanInvokeParam.setInvokeType(BeanInvokeType.RELOAD_MYBATIS.getCode());
        beanInvokeParam.setBeanName(ReflectStringUtils.beanName(ReflectStringUtils.classSimpleName(fulClassName)));
        httpRequestData.fillDataDefaultReq(JacksonUtils.toJSONString(beanInvokeParam));
        try {
            ReadAction.nonBlocking(() -> {
                String resp = "";
                Response response = null;
                try {
                    response = requestExecutor.execute(httpRequestData);
                } catch (IOException e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
                resp = response.getBody();
                return resp;
            }).finishOnUiThread(ModalityState.NON_MODAL, (c) -> {

            }).inSmartMode(project).submit(NonUrgentExecutor.getInstance());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
