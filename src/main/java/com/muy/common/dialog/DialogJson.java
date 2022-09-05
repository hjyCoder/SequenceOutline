package com.muy.common.dialog;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @Author jiyanghuang
 * @Date 2022/8/28 00:32
 */
public class DialogJson extends MRDialog<DialogFormJson> {

    private Consumer<String> consumer;
    public DialogJson(@Nullable Project project, Consumer<String> consumer, String value) {
        super(project, true, "EditorJson", new DialogFormJson(project));
        this.consumer = consumer;
        t.fillText(value);
    }

    @Override
    protected void doOKAction() {
        if(null != consumer){
            consumer.accept(t.fetchText());
            super.doOKAction();
        }
    }
}
