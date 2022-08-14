package com.muy.common.notification;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;

/**
 * @Author jiyanghuang
 * @Date 2022/5/8 6:34 PM
 */
public class SequenceOutlineNotifier {
    private static final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup("SequenceOutline Information", NotificationDisplayType.BALLOON, false);

    private SequenceOutlineNotifier() {
    }

    public static void notify(String content) {
        if (null == content) {
            content = "";
        }
        final Notification notification = NOTIFICATION_GROUP.createNotification("SequenceOutline info", content, NotificationType.INFORMATION, null);
        notification.notify(null);
    }

    public static void notifyError(String content) {
        if (null == content) {
            content = "";
        }
        final Notification notification = NOTIFICATION_GROUP.createNotification("SequenceOutline Error", content, NotificationType.ERROR, null);
        notification.notify(null);
    }
}
