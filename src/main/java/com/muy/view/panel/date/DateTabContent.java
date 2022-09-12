package com.muy.view.panel.date;

import com.muy.view.panel.date.bean.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @Author jiyanghuang
 * @Date 2022/5/31 23:49
 */
@Data
public class DateTabContent {
    private JPanel mainPanel;
    private JTextField tfNow;
    private JTextField tfTimestamp;
    private JComboBox cbUnit;
    private JButton transferLeft;
    private JButton btnTransferRight;
    private JComboBox cbFomat;
    private JTextField tfTimeFormat;
    private JButton buttonNow;
    private JLabel timeTransfer;

    private static String ymd = "yyyy-MM-dd";
    private static String ymdh = "yyyy-MM-dd HH";
    private static String ymdhm = "yyyy-MM-dd HH:mm";
    private static String ymdhms = "yyyy-MM-dd HH:mm:ss";
    private static String ymdhmsz = "yyyy-MM-dd HH:mm:ss zzzz";
    private static String eymdhmsz = "EEEE yyyy-MM-dd HH:mm:ss zzzz";
    private static String ymdhmssz = "yyyy-MM-dd HH:mm:ss.SSSZ";

    /**
     * 最后一步操作的值
     */
    private String lastValue;

    public DateTabContent(){
        initData();
        initEvent();
    }

    public void initData(){
        cbUnit.addItem(new TimeUnitMs());
        cbUnit.addItem(new TimeUnitS());
        cbUnit.addItem(new TimeUnitMin());
        cbUnit.addItem(new TimeUnitHour());

        cbFomat.addItem(new ItemDateFormatYmdhms());
        cbFomat.addItem(new ItemDateFormatYmdhm());
        cbFomat.addItem(new ItemDateFormatYmdh());
        cbFomat.addItem(new ItemDateFormatYmd());
        cbFomat.addItem(new ItemDateFormatYmdhmsz());
        cbFomat.addItem(new ItemDateFormatEymdhmsz());
        cbFomat.addItem(new ItemDateFormatYmdhmssz());

    }

    public void initEvent(){
        buttonNow.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String current = String.valueOf(System.currentTimeMillis());
                tfNow.setText(current);
                lastValue = current;
                tfTimestamp.setText(current);
            }
        });

        btnTransferRight.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = tfTimestamp.getText();
                if(StringUtils.isBlank(text) || !StringUtils.isNumeric(text)){
                    // 通知异常
                    return;
                }
                TimeUnit timeUnitItem = (TimeUnit)cbUnit.getSelectedItem();
                long date = timeUnitItem.transfer(Long.valueOf(text));

                ItemDateFormat itemDateFormat = (ItemDateFormat)cbFomat.getSelectedItem();
                String result = itemDateFormat.format(date);
                lastValue = result;
                tfTimeFormat.setText(result);
            }
        });

        transferLeft.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = tfTimeFormat.getText();

                ItemDateFormat itemDateFormat = (ItemDateFormat)cbFomat.getSelectedItem();
                Long timeLong = itemDateFormat.timeLong(text);

                TimeUnit timeUnitItem = (TimeUnit)cbUnit.getSelectedItem();
                String timeShow = timeUnitItem.timeLongShow(timeLong);

                lastValue = timeShow;
                tfTimestamp.setText(timeShow);
            }
        });

        tfTimeFormat.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                lastValue = tfTimeFormat.getText();
            }
        });

        tfTimestamp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                lastValue = tfTimestamp.getText();
            }
        });
    }
}
