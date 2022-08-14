package com.muy.common.tree;

import javax.swing.*;

/**
 * @Author jiyanghuang
 * @Date 2022/7/17 21:03
 */
public class MTTreeCellBlankRoot implements MTTreeCell{

    @Override
    public String cellShow() {
        return "root";
    }

    @Override
    public Icon iconSelected() {
        return null;
    }

    @Override
    public Icon iconUnselected() {
        return null;
    }

    public static MTTreeCellBlankRoot of(){
        MTTreeCellBlankRoot mtTreeCellBlankRoot = new MTTreeCellBlankRoot();
        return mtTreeCellBlankRoot;
    }
}
