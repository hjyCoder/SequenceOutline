package com.muy.common.dialog;

import com.intellij.openapi.project.Project;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @Author jiyanghuang
 * @Date 2022/8/28 00:32
 */
public class DialogFormConstructorSelect implements DialogFormMark {

    @Getter
    private JComboBox<String> methodSign;

    private Consumer<String> doUpdate;

    public DialogFormConstructorSelect(List<String> items, Consumer<String> doUpdate) {
        this.doUpdate = doUpdate;
        methodSign = new JComboBox<String>(items.toArray(new String[0]));
    }

    @Override
    public JComponent jComponent() {
        return methodSign;
    }

    @Override
    public String title() {
        return "Select Constructor";
    }

    @Override
    public Function<DialogFormConstructorSelect, Pair<Boolean, String>> okFun() {
        return (form) -> {
            doUpdate.accept((String) form.getMethodSign().getSelectedItem());
            return Pair.of(true, null);
        };
    }
}
