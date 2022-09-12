package com.muy.common.dialog;

import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.util.function.Function;

/**
 * @Author jiyanghuang
 * @Date 2022/8/28 00:31
 */
public interface DialogFormMark {

    public default JComponent jComponent() {
        return null;
    }

    /**
     * 点击OK
     * @param <T>
     * @return
     */
    public default <T extends DialogFormMark> Function<T, Pair<Boolean, String>> okFun() {
        return null;
    }

    /**
     * 显示的Title
     * @return
     */
    public default String title(){
        return null;
    }

    public default Project getProject(){
        return null;
    }

    /**
     * 更方便调用的包装
     * @param <T>
     * @return
     */
    public default <T extends DialogFormMark> T ofDialogShow(){
        MRDialog.of(this).show();
        return (T)this;
    }
}
