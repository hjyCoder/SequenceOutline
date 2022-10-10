package com.muy.so.task.copy;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.Copy;

import java.io.File;


public class CopyAgentWrapToJvmSandboxTask extends Copy {

    public static final String NAME = "copyAgentWrapToJvmSandboxTask";
    public static final String TEST_NAME = "copyAgentWrapToJvmSandboxTestTask";

    public static final String TARGET_SUBFOLDER = "sandbox";

    public CopyAgentWrapToJvmSandboxTask() {
        setGroup("intellij");
        configureTask(false);
        from(new File("so-agent-wrap/build/libs/so-agent-wrap.jar"));
    }


    private void configureTask(final boolean test) {
        setDescription("Adds the so-agent-wrap artifacts to sandbox/module");
        into(new File("sandbox/module"));
    }


    public void setTest() {
        configureTask(true);
        final Project project = getProject();
        project.getTasks().getByName(JavaPlugin.TEST_TASK_NAME).dependsOn(this);
    }
}
