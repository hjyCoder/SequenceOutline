package com.muy.view.panel.date.bean;

/**
 * @Author jiyanghuang
 * @Date 2022/6/4 00:17
 */
public abstract class TimeUnit implements ComboBoxItem {

    public abstract Long transfer(Long timeValue);

    public abstract String timeLongShow(Long timeLongMs);

    @Override
    public String toString() {
        return showName();
    }
}
