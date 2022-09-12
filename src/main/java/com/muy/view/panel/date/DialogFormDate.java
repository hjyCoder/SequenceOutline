package com.muy.view.panel.date;

import com.intellij.openapi.project.Project;
import com.muy.common.dialog.DialogFormMark;
import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @Author jiyanghuang
 * @Date 2022/9/11 17:18
 */
public class DialogFormDate implements DialogFormMark {

    private Project project;

    /**
     * 时间显示的界面
     */
    @Getter
    private DateTabContent dateTabContent;
    private Consumer<String> doUpdate;

    public DialogFormDate(Project project, String value, Consumer<String> doUpdate) {
        this.project = project;
        this.doUpdate = doUpdate;
        dateTabContent = new DateTabContent();
        if(NumberUtils.isDigits(value)){
            dateTabContent.getTfTimestamp().setText(value);
        }else{
            dateTabContent.getTfTimeFormat().setText(value);
        }
        dateTabContent.setLastValue(value);
    }

    @Override
    public JComponent jComponent() {
        return dateTabContent.getMainPanel();
    }

    @Override
    public Function<DialogFormDate, Pair<Boolean, String>> okFun() {
        return (form) -> {
            doUpdate.accept(form.getDateTabContent().getLastValue());
            return Pair.of(true, null);
        };
    }

    @Override
    public String title() {
        return "EditDate";
    }
}
