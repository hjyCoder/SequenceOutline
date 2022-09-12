package com.muy.view.panel.date.bean;

/**
 * @Author jiyanghuang
 * @Date 2022/6/4 00:19
 */
public class TimeUnitS extends TimeUnit {

    @Override
    public String showName() {
        return "秒(s)";
    }

    @Override
    public Long transfer(Long timeValue) {
        return timeValue * 1000;
    }

    @Override
    public String timeLongShow(Long timeLongMs) {
        return String.valueOf(timeLongMs/1000);
    }
}
