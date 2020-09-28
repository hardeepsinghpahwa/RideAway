package com.pahwa.rideaway;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;

import androidx.core.app.NotificationCompat;

public class NotificationReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        Intent intentpi = new Intent(context, Home.class);

        PendingIntent pi = PendingIntent.getActivity(context, 101, intentpi, 0);

        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            channel = new NotificationChannel("222", "my_channel", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context, "222")
                        .setContentTitle(intent.getStringExtra("title"))
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(intent.getStringExtra("text")))
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.rideawayiconround))
                        .setSmallIcon(R.drawable.noti)
                        .setContentText(intent.getStringExtra("text"))
                        .setContentIntent(pi);

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        manager.notify(101, builder.build());
    }
}
