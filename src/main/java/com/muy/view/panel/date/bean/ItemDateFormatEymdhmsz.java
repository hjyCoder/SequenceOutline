package com.muy.view.panel.date.bean;


import com.muy.utils.TimeUtils;

/**
 * @Author jiyanghuang
 * @Date 2022/6/4 00:36
 */
public class ItemDateFormatEymdhmsz extends ItemDateFormat {

    @Override
    public String showName() {
        return TimeUtils.EYMDHMSZ;
    }

    @Override
    public Long timeLong(String date) {
        return TimeUtils.eymdhmszLong(date);
    }

    @Override
    public String format(Long date) {
        return TimeUtils.eymdhmsz(date);
    }
}
