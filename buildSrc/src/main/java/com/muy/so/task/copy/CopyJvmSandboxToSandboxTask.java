package com.muy.so.task.copy;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.Copy;

import java.io.File;


public class CopyJvmSandboxToSandboxTask extends Copy {

    public static final String NAME = "copyJvmSandboxToSandboxTask";
    public static final String TEST_NAME = "copyJvmSandboxToSandboxTestTask";

    public static final String TARGET_SUBFOLDER = "sandbox";

    private static final String SANDBOX_PATH = "idea-sandbox/plugins";

    private static final String TEST_SANDBOX_PATH = "idea-sandbox/plugins-test";

    public CopyJvmSandboxToSandboxTask() {
        setGroup("intellij");
        configureTask(false);
        final CopyAgentWrapToJvmSandboxTask gatherTask = (CopyAgentWrapToJvmSandboxTask) getProject().getTasks().getByName(CopyAgentWrapToJvmSandboxTask.NAME);
        dependsOn(gatherTask);
        from(new File("sandbox"));
    }


    private void configureTask(final boolean test) {
        String sandbox = (test ? TEST_SANDBOX_PATH : SANDBOX_PATH);
        setDescription("Adds the JvmSandbox directory to " + sandbox);
        into(new File(getProject().getBuildDir(), sandbox + "/SequenceOutline/" + TARGET_SUBFOLDER));
    }


    public void setTest() {
        configureTask(true);
        final Project project = getProject();
        project.getTasks().getByName(JavaPlugin.TEST_TASK_NAME).dependsOn(this);
    }
}
