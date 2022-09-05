package com.muy.common.dialog;

import javax.swing.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/28 00:31
 */
public interface DialogFormMark {

    public default JComponent jComponent() {
        return null;
    }
}
