package com.muy.view.window.sequence.configuration;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.muy.common.configuration.ProjectConfiguration;
import com.muy.view.window.sequence.bean.TreeNodeModelSequence;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author jiyanghuang
 * @Date 2022/8/4 20:38
 */
@State(
        name = "SequenceConfiguration",
        storages = {
                @Storage(file = "$PROJECT_CONFIG_DIR$/SequenceOutlineSettings.xml")
        }
)
public class SequenceConfiguration extends ProjectConfiguration<SequenceConfiguration, TreeNodeModelSequence> {

    @Getter
    @Setter
    private List<TreeNodeModelSequence> entrances = new LinkedList<>();

    public static SequenceConfiguration getInstance(Project project) {
        return ServiceManager.getService(project, SequenceConfiguration.class);
    }
}
