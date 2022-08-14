package com.muy.utils;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class RepeaterGuiUtils {

    private static final String REPEATER_ICON_FOLDER = "/repeater/icons/";

    public static Icon loadIcon(String iconFileName){
        return IconLoader.findIcon(REPEATER_ICON_FOLDER + iconFileName);
    }

    public static Icon loadIcon(String iconFilename, String darkIconFilename) {
        String iconPath = REPEATER_ICON_FOLDER;
        if (isUnderDarcula()) {
            iconPath += darkIconFilename;
        } else {
            iconPath += iconFilename;
        }
        return IconLoader.findIcon(iconPath);
    }

    public static void installActionGroupInToolBar(DefaultActionGroup actionGroup, JPanel toolBarPanel, ActionManager actionManager, String toolbarName, boolean horizontal) {
        if (actionManager == null) {
            return;
        }

        JComponent actionToolbar = ActionManager.getInstance().createActionToolbar(toolbarName, actionGroup, horizontal).getComponent();
        toolBarPanel.add(actionToolbar, BorderLayout.CENTER);
    }

    public static Dimension enlargeWidth(Dimension preferredSize, double factor) {
        int enlargedWidth = new Double(preferredSize.width * factor).intValue();
        return new Dimension(enlargedWidth, preferredSize.height);
    }

    public static void showNotification(final JComponent component, final MessageType info, final String message, final Balloon.Position position) {
        UIUtil.invokeLaterIfNeeded(() -> JBPopupFactory.getInstance().createBalloonBuilder(new JLabel(message))
                .setFillColor(info.getPopupBackground())
                .createBalloon()
                .show(new RelativePoint(component, new Point(0, 0)), position));
    }

    private static boolean isUnderDarcula() {
        return UIManager.getLookAndFeel().getName().contains("Darcula");
    }
}
