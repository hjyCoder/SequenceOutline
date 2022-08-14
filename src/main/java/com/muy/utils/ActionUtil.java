package com.muy.utils;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.UUID;

/**
 * @Author jiyanghuang
 * @Date 2022/2/19 9:56 PM
 */
public class ActionUtil {

    public static ActionPopupMenu createActionPopupMenu(@NotNull JComponent component, String place, ActionGroup actionGroup){
        ActionManager actionManager = ActionManager.getInstance();
        ActionPopupMenu popupMenu = actionManager.createActionPopupMenu(adjustPlace(place), actionGroup);
        popupMenu.setTargetComponent(component);
        return popupMenu;
    }

    private static String adjustPlace(String place) {
        if (StringUtils.isEmpty(place)) {
            return UUID.randomUUID().toString();
        }
        return place;
    }
}
