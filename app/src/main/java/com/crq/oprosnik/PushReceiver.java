package com.crq.oprosnik;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import java.net.HttpURLConnection;
import java.net.URL;
import ru.evotor.pushNotifications.PushNotificationReceiver;




public class PushReceiver extends PushNotificationReceiver {
    @Override
    public void onReceivePushNotification(Context context, Bundle data, long messageId) {

        String title = data.getString("title", "Внимание новый опрос");
        String text = data.getString("text","Пожалуйста, выполните опросник");
        String priority = data.getString("priority", "");
        String pushId = data.getString("pushId","");

        if(priority != "") {
            int iPriority;
            try {
                iPriority = Integer.parseInt(priority);
            }
            catch (NumberFormatException e)
            {
                iPriority = 2;
            }
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */,
                    intent, PendingIntent.FLAG_ONE_SHOT);


            String channelId = context.getString(R.string.default_notification_channel_id);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context, channelId)
                            .setSmallIcon(R.drawable.ic_stat_bullhorn)
                            .setContentTitle(title)
                            .setContentText(text)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setPriority(iPriority)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

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
            notificationManager.notify(0, n);
        }
        OnReceived(context, pushId);
    }

    private void OnReceived(Context context, String pushId) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
            }

            String android_id = telephonyManager.getDeviceId();
            URL url = new URL("https://kassa.norana.ru/notification/received?deviceId=" + android_id
                    + "&pushId=" + pushId );

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.getResponseCode();
        }
        catch(Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }
}
