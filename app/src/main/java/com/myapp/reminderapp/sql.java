package com.myapp.reminderapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class sql extends SQLiteOpenHelper {
    SQLiteDatabase db;
    Context context;
    private final static String TABLE_NAME = "category";
    private final static String DEFAULT = "default";

    public sql(@Nullable Context context) {
        super(context, "category.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+" (categoryName text NOT NULL)");
        ContentValues cv = new ContentValues();
        cv.put("Default_Table",DEFAULT);
        db.insert(TABLE_NAME,null,cv);
        Object default_value = cv.get("Default_Table");
        db.execSQL("create table "+default_value+" (DateTime text NOT NULL, Task text NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists category");
        onCreate(db);
    }

    public void addData(String table_name){
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("table_name",table_name);
        long save =db.insert(table_name,null,cv);
        if(save==-1)
            showAlert("Failed","Error Occured");
        else{
            db.execSQL("create table "+table_name+"Date text NOT NULL, Time text NOT NULL, Task text NOT NULL");
            showAlert("Success","Inserted successfully");
        }

    }

    public void getData(String table_name, String task){
        db = this.getReadableDatabase();
        Cursor cursor;
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

}