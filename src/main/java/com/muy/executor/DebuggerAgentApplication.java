package com.muy.executor;

import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.target.TargetEnvironmentAwareRunProfileState;
import com.intellij.execution.ui.RunContentDescriptor;
import com.muy.constant.SequenceConstant;
import com.muy.utils.PluginPathUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;

import java.util.List;

/**
 * @Author jiyanghuang
 * @Date 2022/9/4 20:36
 */
public class DebuggerAgentApplication extends GenericDebuggerRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return SequenceConstant.RUNNER_ID;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(SequenceConstant.DEBUGGER_ID) && profile instanceof RunConfigurationBase;
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        if (state instanceof JavaCommandLine) {
            JavaParameters javaParameters = ((JavaCommandLine) state).getJavaParameters();
            javaParameters.getVMParametersList().addAll(agentParams());
        }
        return super.doExecute(state, env);
    }

    /**
     *  javaParameters.getVMParametersList()
     *  格式为
     * -Xdebug
     * -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000
     * -javaagent:${HOME}/sandbox/lib/sandbox-agent.jar=server.port=8820;server.ip=0.0.0.0
     * -Dapp.name=repeater
     * -Dapp.env=dailyInvokeTree
     * @param state
     * @param env
     * @return
     * @throws ExecutionException
     */
    @Override
    protected @NotNull Promise<@Nullable RunContentDescriptor> doExecuteAsync(@NotNull TargetEnvironmentAwareRunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        if (state instanceof JavaCommandLine) {
            JavaParameters javaParameters = ((JavaCommandLine) state).getJavaParameters();
            javaParameters.getVMParametersList().addAll(agentParams());
        }
        Promise<@Nullable RunContentDescriptor> promise = super.doExecuteAsync(state, env);
        promise.onError((throwable) -> {
//            SequenceOutlineNotifier.notifyError("run error promise ");
        });
        promise.onSuccess((rd) -> {
//            SequenceOutlineNotifier.notify("run success promise ");
        });
        return promise;
    }

    /**
     * -javaagent:${HOME}/sandbox/lib/sandbox-agent.jar=server.port=8820;server.ip=0.0.0.0;home=
     * @return
     */
    public List<String> agentParams(){
        List<String> params = Lists.newArrayList();
        Pair<String, String> homeAgentJar = PluginPathUtils.jvmsandboxPath();
        String javaagentParam = "-javaagent:"+homeAgentJar.getRight()+"=server.port=8822;server.ip=127.0.0.1;home=" + homeAgentJar.getLeft();
        params.add(javaagentParam);
        return params;
    }
}
