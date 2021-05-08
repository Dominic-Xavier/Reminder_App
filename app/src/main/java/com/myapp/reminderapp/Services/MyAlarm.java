package com.myapp.reminderapp.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.Settings;
import android.provider.SyncStateContract;

import com.myapp.reminderapp.R;
import com.myapp.reminderapp.userTask.MainActivity;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class MyAlarm extends BroadcastReceiver {

    Notification notification1;
    PendingIntent pendingIntent;
    NotificationManager notificationManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, MyService.class);
        intent1.setAction("Broadcast");
        String tasks = intent.getStringExtra("Task");
        String  action = intent.getAction();
        System.out.println("Broadcast receiver Action is:"+action);
        pendingIntent = PendingIntent.getService(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationManager = context.getSystemService(NotificationManager.class);
        notification1 = new NotificationCompat.Builder(context, MyService.Chanel_Id)
                .setContentTitle("Alarm")
                .setContentText("Your Task is:-"+tasks)
                .setSmallIcon(android.R.drawable.btn_star)
                .addAction(R.mipmap.ic_launcher, "Stop", pendingIntent)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent).build();
        notificationManager.notify(5, notification1);
        context.sendBroadcast(intent1);
        mediaSound(context);
    }

    private void mediaSound(Context context){
        MediaPlayer mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
        mediaPlayer.start();
    }
}