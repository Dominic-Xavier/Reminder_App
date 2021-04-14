package com.myapp.reminderapp.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.myapp.reminderapp.alertORToast.AlertOrToast;
import com.myapp.reminderapp.sql.Sql;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Set;

import androidx.annotation.Nullable;

public class MyService extends Service {

    Sql s = new Sql(this);

    class IBindService extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    private IBinder binder = new IBindService();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(()-> {
            JSONObject js=null;
            JSONArray jsonArray = new JSONArray();
            try {
                Set<String> ids = s.getAllIds();
                for (String id:ids) {
                    js = s.getAllDatas(id);
                    jsonArray.put(js);
                }
                System.out.println("Json Array is:"+jsonArray);
            } catch (JSONException j) {
                j.printStackTrace();
            }
            catch (Exception e){
                System.out.println("Error Happened"+e.getMessage());
            }
        }).start();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void setAlarm(int year, int month, int date, int hour, int min){
        Calendar milliSeconds = Calendar.getInstance();
        milliSeconds.setTimeInMillis(System.currentTimeMillis());
        milliSeconds.set(year,month,date,hour,min);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,milliSeconds.getTimeInMillis(),AlarmManager.INTERVAL_FIFTEEN_MINUTES,pendingIntent);
    }


}
