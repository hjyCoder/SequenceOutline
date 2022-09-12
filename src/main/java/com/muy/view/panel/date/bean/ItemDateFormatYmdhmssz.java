package com.muy.view.panel.date.bean;


import com.muy.utils.TimeUtils;

/**
 * @Author jiyanghuang
 * @Date 2022/6/4 00:36
 */
public class ItemDateFormatYmdhmssz extends ItemDateFormat {

    @Override
    public String showName() {
        return TimeUtils.YMDHMSSZ;
    }

    @Override
    public String format(Long date) {
        return TimeUtils.ymdhmssz(date);
    }

    @Override
    public Long timeLong(String date) {
        return TimeUtils.ymdhmsszLong(date);
    }
}
