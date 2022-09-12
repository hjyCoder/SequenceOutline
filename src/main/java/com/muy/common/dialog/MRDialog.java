package com.muy.common.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.muy.common.notification.SequenceOutlineNotifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.function.Function;

/**
 * @Author jiyanghuang
 * @Date 2022/8/28 00:28
 * 继承于库对象 DialogWrapper
 * 显示Dialog
 * DialogWrapper.show()
 */
public class MRDialog<T extends DialogFormMark> extends DialogWrapper {

    protected T t;

    protected Function<T, Pair<Boolean, String>> okFun;

    protected MRDialog(@Nullable Project project, boolean canBeParent, String title, T t) {
        super(project, canBeParent);
        setTitle(title);
        this.t = t;
        super.init();
    }

    protected MRDialog(@Nullable Project project, boolean canBeParent, String title, T t, Function<T, Pair<Boolean, String>> okFun) {
        super(project, canBeParent);
        setTitle(title);
        this.t = t;
        this.okFun = okFun;
        // 必须调用否则不展示
        super.init();
    }

    /**
     * 点击OK时触发
     */
    @Override
    protected void doOKAction() {
        if (null != okFun) {
            try {
                Pair<Boolean, String> doRe = okFun.apply(t);
                if (doRe.getLeft()) {
                    if (StringUtils.isNotBlank(doRe.getRight())) {
                        SequenceOutlineNotifier.notify(doRe.getRight());
                        return;
                    }
                    super.doOKAction();
                    return;
                }
                if (StringUtils.isNotBlank(doRe.getRight())) {
                    SequenceOutlineNotifier.notifyError(doRe.getRight());
                    return;
                }
            } catch (Exception ex) {
                SequenceOutlineNotifier.notifyError(ex.getMessage());
                return;
            }
        }
        super.doOKAction();
    }

    /**
     * Dialog 显示的面板
     * @return
     */
    @Override
    protected @Nullable JComponent createCenterPanel() {
        return t.jComponent();
    }

    /**
     * 生成对象包装
     * @param t
     * @param <T>
     * @return
     */
    public static <T extends DialogFormMark> MRDialog of(T t) {
        MRDialog<T> dialog = new MRDialog<T>(t.getProject(), true, t.title(), t, t.okFun());
        return dialog;
    }
}
