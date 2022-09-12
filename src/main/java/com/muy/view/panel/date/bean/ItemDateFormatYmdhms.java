package com.muy.view.panel.date.bean;

import com.muy.utils.TimeUtils;

/**
 * @Author jiyanghuang
 * @Date 2022/6/4 00:36
 */
public class ItemDateFormatYmdhms extends ItemDateFormat{

    @Override
    public String showName() {
        return TimeUtils.YMDHMS;
    }

    @Override
    public String format(Long date) {
        return TimeUtils.ymdhms(date);
    }

    @Override
    public Long timeLong(String date) {
        return TimeUtils.ymdhmsLong(date);
    }
}
