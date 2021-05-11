package com.myapp.reminderapp.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

public class StopAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MediaPlayer mediaPlayer = MyAlarm.mediaSound(context);
        String intentTest = intent.getStringExtra("Demo");
        System.out.println("Intent is:"+intentTest);
        mediaPlayer.stop();
    }
}
