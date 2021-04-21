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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(()-> {

                try {
                    String Date, allTime;
                    while (true) {
                        JSONArray jrr = s.allDatas();
                        JSONArray jrrs = decodeJsonArray(jrr);
                        System.out.println("Decoded Json Array is:" + jrrs);

                        for (int i = 0; i < jrrs.length(); i++) {
                            JSONObject jobj = jrrs.getJSONObject(i);
                            Date = jobj.getString("Date");
                            allTime = jobj.getString("Time");
                            String[] dateArr = Date.split("/");
                            String[] timeArr = allTime.split(":");
                            String datess = dateArr[0];
                            String months = dateArr[1];
                            String years = dateArr[2];
                            String hours = timeArr[0];
                            String mins = timeArr[1];

                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
                            LocalDateTime now = LocalDateTime.now();
                            String date_And_Time = dtf.format(now);
                            String[] dateandtime = date_And_Time.split(" ");
                            String dates = dateandtime[0];
                            String time = dateandtime[1];
                            if (dates.compareTo(Date) == 0 && time.compareTo(allTime) == 0) {
                                System.out.println("My Date and time is:"+Date+" "+allTime);
                                System.out.println("System Date and time is:"+dates+" "+time);
                                Calendar milliSeconds = Calendar.getInstance();
                                milliSeconds.setTimeInMillis(System.currentTimeMillis());
                                milliSeconds.set(Integer.parseInt(years), Integer.parseInt(months), Integer.parseInt(datess), Integer.parseInt(hours), Integer.parseInt(mins));
                                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                Intent intents = new Intent(getApplicationContext(), MyAlarm.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intents, 0);
                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, milliSeconds.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
                                Thread.sleep(50000);
                            }
                        }
                        Thread.sleep(5000);
                    }
                } catch (JSONException | InterruptedException e) {
                    e.printStackTrace();
                }
        }).start();
        return START_STICKY;
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
        List<JSONObject> all = new ArrayList<>();
        //Set<String> ids = s.getAllIds();
        for (int i=0;i<jsonArray.length();i++){
            all.add(jsonArray.getJSONObject(i));
            JSONObject obj = jsonArray.getJSONObject(i);
            String date = obj.getString("Date");
            System.out.println("Jobj is:"+date);
        }

        Collections.sort(all, new Comparator<JSONObject>() {
            String ldate, rdate, ltime, rtime, combinedDate, CombinedTime;
            DateFormat f = new SimpleDateFormat("dd/MM/yy HH:mm");
            int all;
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                try {
                    ldate = o1.getString("Date");
                    rdate = o2.getString("Date");
                    ltime = o1.getString("Time");
                    rtime = o2.getString("Time");
                    combinedDate = ldate+" "+ltime;
                    CombinedTime = rdate+" "+rtime;
                    all = f.parse(combinedDate).compareTo(f.parse(CombinedTime));
                    //System.out.println("Value of Cpmparator is:"+all);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
                return all;
            }
        });


        return new JSONArray(all);
    }
}