package com.myapp.reminderapp.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.myapp.reminderapp.sql.Sql;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class MyService extends Service {

    Sql s = new Sql(this);

    Context context;

    static JSONArray jsonArray;

    class IBindService extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    private IBinder binder = new IBindService();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(()-> {
            try {
                JSONArray jrr = s.allDatas();
                System.out.println("Jrr array is:"+jrr);
                JSONArray jrrs = decodeJsonArray(jrr);
                System.out.println("Decoded Json Array is:"+jrrs);
            } catch (JSONException e) {
                System.out.println("Error is:"+e.getMessage());
                e.printStackTrace();
            }
        }).start();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //Fetch values according to
    private JSONArray jsonArray(){
        JSONArray jrr = null;
        try {
            Set<String> ids = s.getAllIds();
            for (String id:ids) {
                if(!id.equals("u_id_2")){
                    jsonArray = s.getAllDatas(id);
                    jrr=new JSONArray(jsonArray);
                }
            }

            System.out.println("Json Array is:"+jrr);
        } catch (JSONException j) {
            j.printStackTrace();
        }
        catch (Exception e){
            System.out.println("Error Happened"+e.getMessage());
        }
        return jrr;
    }

    private JSONArray decodeJsonArray(JSONArray jsonArray) throws JSONException {
        System.out.println("Decodeds Json Array is:"+jsonArray);
        JSONObject[] jobj = new JSONObject[jsonArray.length()];
        for (int i=0;i<jobj.length;i++){
            jobj[i] = jsonArray.getJSONObject(i);
        }
        List<JSONObject> all = new ArrayList<>();
        //Set<String> ids = s.getAllIds();
        for (int i=0;i<jsonArray.length();i++){
            all.add(jsonArray.getJSONObject(i));
            JSONObject obj = jsonArray.getJSONObject(i);
            String date = obj.getString("Date");
            System.out.println("Jobj is:"+date);
        }

        Collections.sort(all, new Comparator<JSONObject>() {
            String ldate, rdate;
            DateFormat f = new SimpleDateFormat("dd/MM/yy");
            int all;
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                try {
                    ldate = o1.getString("Date");
                    rdate = o2.getString("Date");
                    all = f.parse(ldate).compareTo(f.parse(rdate));
                    System.out.println("Value of Cpmparator is:"+all);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
                return all;
            }
        });

        return new JSONArray(all);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setAlarm(int year, int month, int date, int hour, int min){
        DateTimeFormatter dtf = null;
        LocalDateTime now = null;
        dtf = DateTimeFormatter.ofPattern("MM/dd/YY HH:mm");
        now = LocalDateTime.now();
        String dates = dtf.format(now);

        Calendar milliSeconds = Calendar.getInstance();
        milliSeconds.setTimeInMillis(System.currentTimeMillis());
        milliSeconds.set(year,month,date,hour,min);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,milliSeconds.getTimeInMillis(),AlarmManager.INTERVAL_FIFTEEN_MINUTES,pendingIntent);
    }
}