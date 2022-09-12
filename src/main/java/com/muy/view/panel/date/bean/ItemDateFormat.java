package com.muy.view.panel.date.bean;

/**
 * @Author jiyanghuang
 * @Date 2022/6/4 00:30
 */
public abstract class ItemDateFormat implements ComboBoxItem{

    public abstract String format(Long date);

    public abstract Long timeLong(String date);

    @Override
    public String toString() {
        return showName();
    }
}
