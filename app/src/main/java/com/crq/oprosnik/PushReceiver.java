package com.crq.oprosnik;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import ru.evotor.pushNotifications.PushNotificationReceiver;

public class PushReceiver extends PushNotificationReceiver {
    @Override
    public void onReceivePushNotification(Context context, Bundle data, long messageId) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);



        String channelId = context.getString(R.string.default_notification_channel_id);
        // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_stat_check_circle_outline)
                        .setContentTitle("Текст заголовка")
                        .setContentText("Новый опрос, короны и вирусы!")
                        .setAutoCancel(true)
                        // .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(notificationManager == null)
        {
            Toast.makeText(context, "NotificationManager is null", Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId("com.crq.oprosnik");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "com.crq.oprosnik",
                    "Oprosnik",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Notification n = notificationBuilder.build();

        // Toast.makeText(context,"до этого места работает",      Toast.LENGTH_SHORT).show();
        try {
            notificationManager.notify(0, n);
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),      Toast.LENGTH_SHORT).show();
        }
    }
}
