 package com.myapp.reminderapp.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.myapp.reminderapp.alertORToast.AlertOrToast;
import com.myapp.reminderapp.userTask.MainActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class sql extends SQLiteOpenHelper {
    public SQLiteDatabase db;
    Context context;
    ContentValues cv;
    Cursor cursor;

    public sql(@Nullable Context context) {
        super(context, "demo.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        //This is the format
        //db.execSQL("create table Category(CategoryName text)");
        db.execSQL("create table "+COLUMN.Category.toString()+" ("+COLUMN.Category_Name.toString()+" text NOT NULL)");
        db.execSQL("create table "+COLUMN.Defaults.toString()+" ("+COLUMN.Task.toString()+" text NOT NULL,"+COLUMN.Date.toString()+" text NOT NULL,"+COLUMN.Time.toString()+" text NOT NULL)");
        db.execSQL("create table "+COLUMN.Finished.toString()+" ("+COLUMN.Task.toString()+" text NOT NULL,"+COLUMN.Date.toString()+" text NOT NULL,"+COLUMN.Time.toString()+" text NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Category");
        onCreate(db);
    }

    public List<String> addCategory(String Category){
        db = this.getWritableDatabase();
        if(check_duplicate_Table(Category)){
            cv = new ContentValues();
            cv.put(COLUMN.Category_Name.toString(), Category);
            long save = db.insert(COLUMN.Category.toString(),null,cv);
            if(save!=-1){
                new AlertOrToast(context).showAlert("Success", "Value is inserted into database");
                db.execSQL("create table "+Category+" ("+COLUMN.Task.toString()+" text NOT NULL,"+COLUMN.Date.toString()+" text,"+COLUMN.Time.toString()+" text)");
            }
            else
                new AlertOrToast(context).showAlert("Faled","Values did not entered into the database");
        }
        else
            new AlertOrToast(context).toastMessage("This Category already exists");
        return getCategory();
    }

    public void addData(String categoryName, String Task){
        long save;
        //here categoryName is table name
        db = this.getWritableDatabase();
        cv = new ContentValues();
        cv.put(COLUMN.Task.toString(), Task);
        save = db.insert(categoryName, null, cv);
        if(save==-1)
            new AlertOrToast(context).showAlert("Failed","Error Occured");
        else
            new AlertOrToast(context).showAlert("Success","Values Inserted Successfully");
    }

    public void addData(String categoryName ,String Task, String date, String time){
        //here categoryName is table name
        db = this.getWritableDatabase();
        cv = new ContentValues();
        cv.put(COLUMN.Date.toString(),date);
        cv.put(COLUMN.Time.toString(),time);
        String where = Task+"=?";
        String[] whereArgs = new String[] {date,time};
        int a = db.update(categoryName, cv,where,whereArgs);
        if(a>0)
            new AlertOrToast(context).showAlert("Success","Values updated");
        else
            new AlertOrToast(context).showAlert("Failed","Values did not update");
    }

    public String getTask(String catagoryName, String task){
        db = this.getReadableDatabase();
        String tasks = "";
        String arr[] = {COLUMN.Task.toString()};
        int i=1;
        try {
            cursor = db.query(catagoryName,arr,null,null,null,null,null);
            while (cursor.moveToNext()){
                String row = cursor.getString(i);
                if(row.equals(task)){
                    tasks = cursor.getString(i);
                    break;
                }
                i++;
            }
        }catch (SQLException sql){
            new AlertOrToast(context).showAlert("Error",sql.getMessage());
        }
        return tasks;
    }

    public void getCategoryData(String category){
        db = this.getReadableDatabase();
        String arr[] = {COLUMN.id.toString(),COLUMN.Category_Name.toString()};
        cursor = db.query(COLUMN.Category_Name.toString(),arr,null,null,null,null,null);
        while (cursor.moveToNext()){
            String categorys = cursor.getString(1);
            if(!categorys.equals(category)){
                 break;
            }
        }
    }

    public void updateTask(String catagoryName, String task){
        db = this.getWritableDatabase();
        cv = new ContentValues();
        cv.put(COLUMN.Task.toString(),task);
        long l = db.update(catagoryName,cv,null,null);
        if(l!=0)
            new AlertOrToast(context).showAlert("Success","Value updated");
        else
            new AlertOrToast(context).showAlert("Error","Error Occured");
    }

    public boolean check_duplicate_Table(String tableName){
        boolean verify_Table_Name;
        int count=0;
        db = this.getReadableDatabase();
        String column[] = {COLUMN.Category_Name.toString()};
        cursor = db.query(COLUMN.Category.toString(),column,null,null,null,null,null);
        while (cursor.moveToNext()){
            String columnName = cursor.getString(0);
            System.out.println("Column Name is:"+columnName);
            if(columnName.equals(tableName)) {
                count++;
                break;
            }
        }
        if(count>0)
            verify_Table_Name = false;
        else
            verify_Table_Name = true;
        return verify_Table_Name;
    }

    public List<String> getTasks(){
        MainActivity activity = new MainActivity();
        String tableName = activity.getCategory_name();
        String[] arr = {tableName};
        List<String> list = new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName,arr,null,null,null,null,null);
        while (cursor.moveToNext())
            list.add(cursor.getString(0));
        return list;
    }

    public List<String> getCategory(){
        List<String> category_list = new ArrayList<>();
        cursor = db.query(COLUMN.Category.toString(), new String[]{COLUMN.Category_Name.toString()},null,null,null,null,null);
        while (cursor.moveToNext())
            category_list.add(cursor.getString(0));
        return category_list;
    }
}