package com.example.luckyleaf;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class TimedBroadcast extends BroadcastReceiver {
    final String unlockAlarmKey = "unlockKey";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("123","TimedBroadcast");
        if (intent!=null && intent.getExtras()!=null && intent.getExtras().containsKey(unlockAlarmKey))
        {
            String title = intent.getExtras().getString(unlockAlarmKey);
            Intent mqttService = new Intent(context, BackGroundService.class);
            mqttService.putExtra(unlockAlarmKey,title);
            mqttService.putExtra(BackGroundService.SHOW_NOTIFACTION, "");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(mqttService);
            }
            else
                context.startService(mqttService);
        }
    }
}
