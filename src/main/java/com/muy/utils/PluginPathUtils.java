package com.muy.utils;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.muy.common.notification.SequenceOutlineNotifier;
import com.muy.constant.SequenceConstant;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

/**
 * @Author jiyanghuang
 * @Date 2022/9/4 21:54
 */
public class PluginPathUtils {

    public static final String JVM_SANDBOX_HOME_NAME = "jvmsandbox";


    @NotNull
    public static Optional<String> agentPath() {
        final Path[] path = new Path[1];
        try {
            IdeaPluginDescriptor[] plugins = PluginManager.getPlugins();
            Optional<IdeaPluginDescriptor> anyPlugin = Arrays.stream(plugins)
                    .filter(t -> PluginId.getId(SequenceConstant.PLUGIN_ID).equals(t.getPluginId())).findAny();

            try {
                anyPlugin.map(plugin -> path[0] = plugin.getPluginPath());
            } catch (Throwable e) {
                SequenceOutlineNotifier.notifyError(e.getMessage());
            }
        } catch (Throwable e) {
            SequenceOutlineNotifier.notifyError(e.getMessage());
            return Optional.empty();
        }

        try {
            return Files.walk(path[0])
                    .filter(file -> file.toFile().getName().equals("agent.jar"))
                    .findAny().flatMap(t -> Optional.of(t.toAbsolutePath().toString()));
        } catch (IOException e) {
            SequenceOutlineNotifier.notifyError(e.getMessage());
            return Optional.empty();
        }
    }

    @NotNull
    public static Pair<String, String> jvmsandboxPath() {
        final Path[] path = new Path[2];
        try {
            IdeaPluginDescriptor pluginDescriptor = PluginManagerCore.getPlugin(PluginId.getId(SequenceConstant.PLUGIN_ID));
            String absolutePathStr = pluginDescriptor.getPluginPath().toAbsolutePath().toString();
            String agentJar = absolutePathStr + File.separatorChar + "sandbox/lib/sandbox-agent.jar";
            String sandboxHome = absolutePathStr + File.separatorChar + "sandbox";
            return Pair.of(sandboxHome, agentJar);
        } catch (Throwable e) {
            SequenceOutlineNotifier.notifyError(e.getMessage());
            return Pair.of("", "");
        }
    }

    /**
     * 由于目录可能有空格，因此用引号来包装
     * @param path
     * @return
     */
    public static String wrapQuotationMarks(String path) {
        return "\"" + path + "\"";
    }
}
