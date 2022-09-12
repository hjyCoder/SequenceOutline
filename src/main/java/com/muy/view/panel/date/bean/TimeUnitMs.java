package com.muy.view.panel.date.bean;

/**
 * @Author jiyanghuang
 * @Date 2022/6/4 00:19
 */
public class TimeUnitMs extends TimeUnit {

    @Override
    public String showName() {
        return "毫秒(ms)";
    }

    @Override
    public Long transfer(Long timeValue) {
        return timeValue;
    }

    @Override
    public String timeLongShow(Long timeLongMs) {
        return String.valueOf(timeLongMs);
    }
}
