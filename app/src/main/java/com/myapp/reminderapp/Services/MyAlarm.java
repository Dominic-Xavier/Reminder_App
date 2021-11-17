package com.myapp.reminderapp.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.RequiresApi;

public class MyAlarm extends BroadcastReceiver {
    static MediaPlayer mediaPlayer;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, StopAlarm.class);
        mediaSound(context).start();
    }

    public static MediaPlayer mediaSound(Context context){
        mediaPlayer = MediaPlayer.create(context.getApplicationContext(), Settings.System.DEFAULT_RINGTONE_URI);
        return mediaPlayer;
    }
}