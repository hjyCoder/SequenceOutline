package com.muy.utils;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.IconManager;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class GuiUtils {
    private static final String ICON_FOLDER = "/icons/";

    public static final Icon SEQUENCE_OUTLINE = loadIcon("SequenceOutline.png");

    public static final Icon PLANT_UML = loadIcon("plantUml.png");

    public static Icon loadIcon(String iconFilename) {
        return IconLoader.findIcon(ICON_FOLDER + iconFilename, GuiUtils.class);
    }

    public static Icon loadIcon(String iconFilename, String darkIconFilename) {
        String iconPath = ICON_FOLDER;
        if (isUnderDarcula()) {
            iconPath += darkIconFilename;
        } else {
            iconPath += iconFilename;
        }
        return IconLoader.findIcon(iconPath, GuiUtils.class);
    }

    /**
     * 生成 ActionGroup 组
     * @param actionGroup
     * @param toolBarPanel
     * @param actionManager
     * @param toolbarName
     * @param horizontal
     */
    public static void installActionGroupInToolBar(DefaultActionGroup actionGroup, JPanel toolBarPanel, ActionManager actionManager, String place, boolean horizontal) {
        if (actionManager == null) {
            return;
        }

//        JComponent actionToolbar = ActionManager.getInstance().createActionToolbar(place, actionGroup, horizontal).getComponent();
//        toolBarPanel.add(actionToolbar, BorderLayout.CENTER);
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(place, actionGroup, horizontal);
        toolBarPanel.add(actionToolbar.getComponent(), BorderLayout.CENTER);
        actionToolbar.setTargetComponent(toolBarPanel);
//        toolBarPanel.add(actionToolbar, BorderLayout.CENTER);

    }

    public static Dimension enlargeWidth(Dimension preferredSize, double factor) {
        int enlargedWidth = Double.valueOf(preferredSize.width * factor).intValue();
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
