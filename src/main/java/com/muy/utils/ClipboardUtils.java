package com.muy.utils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 * @Author jiyanghuang
 * @Date 2022/4/11 2:00 AM
 */
public class ClipboardUtils {

    /**
     * 只处理String
     *
     * @return
     */
    public static String fetchStringFromClip() {
        try {

            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable transferable = clipboard.getContents(null);
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) transferable.getTransferData(DataFlavor.stringFlavor);
            }
            return "";
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 把内容填充到粘贴板中
     *
     * @param clipContent
     */
    public static void fillStringToClip(String clipContent) {
        StringSelection selection = new StringSelection(clipContent);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }
}
