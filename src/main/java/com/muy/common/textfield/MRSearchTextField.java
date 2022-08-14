package com.muy.common.textfield;

import com.intellij.ui.SearchTextField;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.function.Function;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 22:41
 */
public class MRSearchTextField extends SearchTextField {

    private Function<String,Boolean> searchFun;

    public MRSearchTextField(Function<String,Boolean> searchFun){
        super();
    }

    @Override
    protected boolean preprocessEventForTextField(KeyEvent e) {
        boolean result = super.preprocessEventForTextField(e);
        if (result) {
            return result;
        }
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            System.out.println("handler key enter");
            e.consume();
            getHistory();
            addCurrentTextToHistory();
            // 如果key查询失败时，则直接去掉添加的 String，重新Set自然会更新了
//            setHistory();
            searchFun.apply(getText());
            return true;
        }
        return false;
    }

    @Override
    protected Runnable createItemChosenCallback(JList list) {
        return () -> {
            final String value = (String)list.getSelectedValue();
            getTextEditor().setText(value != null ? value : "");
            boolean result = searchFun.apply(value);
            if(result){
                addCurrentTextToHistory();
            }
        };
    }
}
