package com.myapp.reminderapp.Services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.provider.SyncStateContract;

import com.myapp.reminderapp.R;
import com.myapp.reminderapp.sql.Sql;
import com.myapp.reminderapp.userTask.MainActivity;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.work.Constraints;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MyService extends Service {

    public static final String Chanel_Id ="1";
    private static boolean servicestarted = true;

    boolean isNotified = false;

    Notification notification1;
    NotificationManager notificationManager;
    Intent intent1, stop_Alarm;

    public AlarmManager alarmManager;

    PendingIntent pendingIntent, notificationIntent, ararm_Cancel_Intent;
    static String tasks;

    static JSONArray jsonArray;

    Sql s = new Sql(this);

    public static boolean isServicestarted() {
        return servicestarted;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        servicestarted = false;
        createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, Chanel_Id)
                .setContentTitle("Running")
                .setContentText("Reminder app is running")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(android.R.drawable.ic_lock_idle_low_battery)
                .setOngoing(false)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent).build();
        getDatas();
        showNotification();
        startForeground(1, notification);
        return START_STICKY;
    }

    private void createNotificationChannel(){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel notificationChannel = new NotificationChannel(Chanel_Id,"Reminder App is running", NotificationManager.IMPORTANCE_HIGH);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(notificationChannel);
            }
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private JSONArray decodeJsonArray(JSONArray jsonArray) throws JSONException {
        List<JSONObject> all = new ArrayList<>();
        //Set<String> ids = s.getAllIds();
        for (int i=0;i<jsonArray.length();i++){
            all.add(jsonArray.getJSONObject(i));
        }

        Collections.sort(all, new Comparator<JSONObject>() {
            String ldate, rdate, ltime, rtime, combinedDate, CombinedTime;
            DateFormat f = new SimpleDateFormat("dd/MM/yy HH:mm");
            int sorted;
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                try {
                    ldate = o1.getString("Date");
                    rdate = o2.getString("Date");
                    ltime = o1.getString("Time");
                    rtime = o2.getString("Time");
                    combinedDate = ldate+" "+ltime;
                    CombinedTime = rdate+" "+rtime;
                    sorted = f.parse(combinedDate).compareTo(f.parse(CombinedTime));
                    //System.out.println("Value of Cpmparator is:"+all);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
                return sorted;
            }
        });

        for (int i=0;i<all.size();i++){
            JSONObject jobj = all.get(i);
            String userDate = jobj.getString("Date");
            String current_Date = currentDate();
            System.out.println("Current Date is:"+current_Date+" "+userDate);
            System.out.println("Coparing Dates:"+current_Date.compareTo(userDate));
            if (current_Date.compareTo(userDate)==-2)
                all.remove(i);
        }
        return new JSONArray(all);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public synchronized void getDatas(){
        Thread t1 = new Thread(()-> {
            try {
                String Date, allTime;
                while (true) {
                    JSONArray jrr = s.allDatas();
                    JSONArray jrrs = decodeJsonArray(jrr);
                    System.out.println("Decoded Json Array is:" + jrrs);
                    for (int i = 0; i < jrrs.length(); i++) {
                        JSONObject jobj = jrrs.getJSONObject(i);
                        tasks = jobj.getString("Task");
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
                        System.out.println("Time is:"+time);

                        //Checking weather current time matches with user given time
                        if (dates.compareTo(Date) == 0 && time.compareTo(allTime) == 0) {
                            Calendar milliSeconds = Calendar.getInstance();
                            milliSeconds.setTimeInMillis(System.currentTimeMillis());
                            milliSeconds.set(Integer.parseInt(years), Integer.parseInt(months), Integer.parseInt(datess), Integer.parseInt(hours), Integer.parseInt(mins));
                            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            Intent intents = new Intent(getApplicationContext(), MyAlarm.class);
                            intents.putExtra("Task",tasks);
                            intents.setAction("Broadcast");
                            System.out.println("My Tasks:"+tasks);

                            pendingIntent = PendingIntent.getBroadcast(this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);

                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, milliSeconds.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);

                            notificationManager = getSystemService(NotificationManager.class);
                            notification1 = new NotificationCompat.Builder(this, Chanel_Id)
                                    .setContentTitle("It's Time to do your task")
                                    .setContentText("Your Task is:-" + tasks)
                                    .setSmallIcon(android.R.drawable.btn_star)
                                    .addAction(R.mipmap.ic_launcher, "Stop Alarm", notificationIntent)
                                    .setAutoCancel(true)
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                    .setContentIntent(notificationIntent).build();
                            notificationManager.notify(5, notification1);

                            intent1 = new Intent(this, StopAlarm.class);
                            intent1.putExtra("Demo", "Intent is passed...!");
                            notificationIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);

                            stop_Alarm = new Intent(this, StopAlarm.class);

                            ararm_Cancel_Intent = PendingIntent.getBroadcast(this, 0, stop_Alarm, 0);

                            Thread.sleep(50000);
                            alarmManager.cancel(ararm_Cancel_Intent);
                        }
                    }
                    Thread.sleep(5000);
                }
            } catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String currentDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy");
        LocalDateTime now = LocalDateTime.now();
        String current_Date = dtf.format(now);
        return current_Date;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private synchronized void showNotification() {
        Thread t2 = new Thread(() -> {

        });
        t2.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}