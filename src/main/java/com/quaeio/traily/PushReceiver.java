package com.quaeio.traily;

/**
 * Created by simeon.garcia on 11/28/2017.
 */

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.content.Context;
import android.media.RingtoneManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Random;

public class PushReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        String notificationTitle = "Pushy";
        String notificationText = "Test notification";

        // Attempt to extract the "message" property from the payload: {"message":"Hello World!"}
        if (intent.getStringExtra("message") != null) {
            notificationText = intent.getStringExtra("message");
        }


        Intent _intent = new Intent(context, MainActivity.class);
        _intent.putExtra("message", intent.getStringExtra("message"));
        _intent.putExtra("messageid", intent.getStringExtra("messageid"));


        _intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, _intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        // Prepare a notification with vibration, sound and lights
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setLights(Color.RED, 1000, 1000)
                .setVibrate(new long[]{0, 400, 250, 400})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        // Build the notification and display it
        Random r = new Random();
        int id = r.nextInt(80 - 65) + 65;
        mNotifyMgr.notify(id, mBuilder.build());
    }
}
