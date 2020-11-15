package com.myapp.reminderapp.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class sql extends SQLiteOpenHelper {
    SQLiteDatabase db;
    Context context;
    ContentValues cv;

    public sql(@Nullable Context context) {
        super(context, "category.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        db.execSQL("create table "+COLUMN.Category_Name+" (id text NOT NULL PRIMARY KEY, categoryName text NOT NULL)");
        ContentValues cv = new ContentValues();
        cv.put("id",idGenerator());
        cv.put("Default_Table", COLUMN.Default.toString());
        cv.put("Finished_table",COLUMN.Finished.toString());
        long table = db.insert(COLUMN.Category_Name.toString(),null,cv);
        if(table!=-1){
            Object default_value = cv.get("Default_Table");
            Object Finished_table = cv.get("Finished_table");
            db.execSQL("create table "+default_value+" (ID text PRIMARY KEY NOT NULL,TASK text NOT NULL , DATE text NOT NULL, Time text NOT NULL)");
            db.execSQL("create table "+Finished_table+" (ID text PRIMARY KEY NOT NULL,TASK text NOT NULL , DATE text NOT NULL, Time text NOT NULL)");
        }
        else
            showAlert("SQL Error","Some error occured");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists category");
        onCreate(db);
    }

    public void addData(String categoryName, String Task, String date, String time){
        long save;
        //here categoryName is table name
        db = this.getWritableDatabase();
        cv = new ContentValues();
        cv.put(COLUMN.id.toString(),idGenerator());
        cv.put(COLUMN.Task.toString(), Task);
        cv.put(COLUMN.Date.toString() ,date);
        cv.put(COLUMN.Time.toString(), time);
        try {
            save = db.insert(idGenerator(), null, cv);
            if(save==-1)
                showAlert("Failed","Error Occured");
            else
                showAlert("Success","Values Inserted Successfully");
        }

        catch (Exception e){
            db.execSQL("create table "+cv.getAsString("id")+" id text PRIMARY KEY NOT NUll, Task text NOT NULL, Date text NOT NULL, Time text NOT NULL");
            save = db.insert(categoryName,null,cv);
            if(save!=-1)
                showAlert("Success","Inserted successfully");
            else
                showAlert("Failed","Error Occured");
            }
    }

    public String getTask(String catagoryName, String task){
        db = this.getReadableDatabase();
        String tasks = "";
        String arr[] = {COLUMN.Task.toString()};
        int i=1;
        try {
            Cursor cursor = db.query(catagoryName,arr,null,null,null,null,null);
            while (cursor.moveToNext()){
                String row = cursor.getString(i);
                if(row.equals(task)){
                    tasks = cursor.getString(i);
                    break;
                }
                i++;
            }
        }catch (SQLException sql){
            showAlert("Error",sql.getMessage());
        }
        return tasks;
    }

    public AlertDialog showAlert(String title, String message){
        AlertDialog.Builder alert = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok",null);
        AlertDialog dialog = alert.create();
        dialog.show();
        return dialog;
    }

    public String idGenerator(){
        int count=1;
        Cursor allIds = db.rawQuery("select * from "+COLUMN.Category_Name.toString(),null);
        while (allIds.moveToNext()){
            count++;
        }
        String id = "u_id_"+count;
        return id;
    }

    public void getCategoryData(String category){
        db = this.getReadableDatabase();
        String arr[] = {COLUMN.id.toString(),COLUMN.Category_Name.toString()};
        Cursor allIds = db.query(COLUMN.Category_Name.toString(),arr,null,null,null,null,null);
        while (allIds.moveToNext()){
            String categorys = allIds.getString(1);
            if(!categorys.equals(category)){
                cv.put("id",idGenerator()+1);
                db.insert(idGenerator(),null,cv);
            }
        }
    }

    public void updateTask(String catagoryName, String task){
        db = this.getWritableDatabase();
        cv = new ContentValues();
        cv.put(COLUMN.Task.toString(),task);
        long l = db.update(catagoryName,cv,null,null);
        if(l!=0)
            showAlert("Success","Value updated");
        else
            showAlert("Error","Error Occured");
    }
}
enum COLUMN{
    Category_Name,
    Default,
    Finished,
    id,
    Task,
    Date,
    Time
}
