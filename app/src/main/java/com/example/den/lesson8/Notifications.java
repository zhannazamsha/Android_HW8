package com.example.den.lesson8;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

public class Notifications {

    public static NotificationCompat.Builder getNotification(Context context) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "default");
        mBuilder.setContentTitle("Its time to look at new inspiring images!")
                .setSmallIcon(R.drawable.white_notification_icon)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE);
        return mBuilder;
    }

    public static void showNotificationAfterDelay(final NotificationCompat.Builder notification, Context context) {

        final NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {
            String id = "my_channel_01";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, "Channel name",importance);
            mChannel.enableLights(true);
            mNotificationManager.createNotificationChannel(mChannel);
            notification.setChannelId(id);
        }

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mNotificationManager.notify(0, notification.build());
            }
        }, 86400000);
    }
}
