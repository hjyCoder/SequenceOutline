package com.muy.common.notification;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;

/**
 * @Author jiyanghuang
 * @Date 2022/5/8 6:34 PM
 */
public class SequenceOutlineNotifier {

    private SequenceOutlineNotifier() {
    }

    public static void notify(String content) {
        if (null == content) {
            content = "";
        }
        NotificationGroup notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("SequenceOutlineNotifier");
        final Notification notification = notificationGroup.createNotification("SequenceOutline info", content, NotificationType.INFORMATION);
        notification.notify(null);
    }

    public static void notifyError(String content) {
        if (null == content) {
            content = "";
        }
        NotificationGroup notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("SequenceOutlineNotifier");
        final Notification notification = notificationGroup.createNotification("SequenceOutline Error", content, NotificationType.ERROR);
        notification.notify(null);
    }
}
