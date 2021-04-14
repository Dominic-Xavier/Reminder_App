  package com.myapp.reminderapp.sql;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.myapp.reminderapp.alertORToast.AlertOrToast;
import com.myapp.reminderapp.userTask.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.Nullable;

public class Sql extends SQLiteOpenHelper {
    public static SQLiteDatabase db;
    Context context;
    ContentValues cv;
    static Cursor cursor, cursors;
    static Map<String, Set<String>> map;
    JSONObject jsonObject;
    JSONArray jsonArray;

    public Sql(@Nullable Context context) {
        super(context, "category.db", null, 1);
        this.context = context;
    }

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use for locating paths to the the database
     * @param name    of the database file, or null for an in-memory database
     * @param factory to use for creating cursor objects, or null for the default
     * @param version number of the database (starting at 1); if the database is older,
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    public Sql(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        //This is the format
        //db.execSQL("create table Category(CategoryName text)");
        db.execSQL("create table " + COLUMN.Category.toString() + " (" + COLUMN.id.toString() + " text NOT NULL," + COLUMN.Category_Name.toString() + " text NOT NULL)");
        cv = new ContentValues();
        cv.put(COLUMN.id.toString(), "u_id_1");
        cv.put(COLUMN.Category_Name.toString(), COLUMN.Defaults.toString());
        long values = db.insert(COLUMN.Category.toString(), null, cv);
        cv.put(COLUMN.id.toString(), "u_id_2");
        cv.put(COLUMN.Category_Name.toString(), COLUMN.Finished.toString());
        long value = db.insert(COLUMN.Category.toString(), null, cv);
        if(value!=-1 && values!=-1){
            db.execSQL("create table u_id_1" + " (" + COLUMN.Task.toString() + " text," + COLUMN.Date.toString() + " text," + COLUMN.Time.toString() + " text)");
            db.execSQL("create table u_id_2" + " (" + COLUMN.Task.toString() + " text," + COLUMN.Date.toString() + " text," + COLUMN.Time.toString() + " text)");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Category");
        onCreate(db);
    }

    public List<String> addCategory(String Category) {
        db = this.getWritableDatabase();
        if (check_duplicate_Table(Category)) {
            cv = new ContentValues();
            String ID_NUMBER = idGenerator();
            cv.put(COLUMN.id.toString(),ID_NUMBER);
            cv.put(COLUMN.Category_Name.toString(), Category);
            long save = db.insert(COLUMN.Category.toString(), null, cv);
            if (save != -1) {
                new AlertOrToast(context).showAlert("Success", "Category is added into database");
                db.execSQL("create table "+ID_NUMBER+" (" + COLUMN.Task.toString() + " text," + COLUMN.Date.toString() + " text," + COLUMN.Time.toString() + " text)");
            } else
                new AlertOrToast(context).showAlert("Faled", "Category is not added into database");
        } else
            new AlertOrToast(context).toastMessage("This Category already exists");
        return getCategory();
    }

    public void addData(String categoryName, String Task) {
        long save;
        //here categoryName is table name
        db = this.getWritableDatabase();
        cv = new ContentValues();
        cv.put(COLUMN.Task.toString(), Task);
        save = db.insert(categoryName, null, cv);
        if (save == -1)
            new AlertOrToast(context).showAlert("Failed", "Error Occured");
        else
            new AlertOrToast(context).toastMessage("Values entered successfully...!");
    }

    public void updateData(String tableName, String Task, String date, String time) {
        //here categoryName is table name
        db = this.getWritableDatabase();
        cv = new ContentValues();
        cv.put(COLUMN.Date.toString(), date);
        cv.put(COLUMN.Time.toString(), time);
        String where = COLUMN.Task.toString() + "=?";
        String[] whereArgs = {Task};
        try {
            int update = db.update(tableName,cv,where,whereArgs);
            if(update!=0)
                new AlertOrToast(context).showAlert("Success","Values Updated successfully");
            else
                new AlertOrToast(context).showAlert("Error","Values did not updated");
        }catch (Exception e){
            new AlertOrToast(context).showAlert("Error",""+e.getMessage());
        }
    }

    public void updateTask(String catagoryName, String task) {
        db = this.getWritableDatabase();
        cv = new ContentValues();
        cv.put(COLUMN.Task.toString(), task);
        int l = db.update(catagoryName, cv, null, null);
        if (l != 0)
            new AlertOrToast(context).showAlert("Success", "Value updated");
        else
            new AlertOrToast(context).showAlert("Error", "Error Occured");
    }

    public boolean check_duplicate_Table(String tableName) {
        boolean verify_Table_Name;
        int count = 0;
        db = this.getReadableDatabase();
        String column[] = {COLUMN.Category_Name.toString()};
        cursor = db.query(COLUMN.Category.toString(), column, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String columnName = cursor.getString(0);
            System.out.println("Column Name is:" + columnName);
            if (columnName.equals(tableName)) {
                count++;
                break;
            }
        }
        if (count > 0)
            verify_Table_Name = false;
        else
            verify_Table_Name = true;
        return verify_Table_Name;
    }

    public List<String> getCategory() {
        List<String> category_list = new ArrayList<>();
        db = this.getReadableDatabase();
        cursor = db.query(COLUMN.Category.toString(), new String[]{COLUMN.id.toString(),COLUMN.Category_Name.toString()}, null, null, null, null, null);
        while (cursor.moveToNext())
            category_list.add(cursor.getString(1));
        return category_list;
    }

    //Want to get all the category as key and all tasks as values
    public JSONObject allDatas() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        map = new LinkedHashMap<>();
        Set<String> add_task = new LinkedHashSet<>();
        Set<String> add_tasks = new LinkedHashSet<>();
        db = this.getReadableDatabase();
        cursor = db.query(COLUMN.Category.toString(), new String[]{COLUMN.id.toString(), COLUMN.Category_Name.toString()}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            //Here idNumber is TableName
            String idNumber = cursor.getString(0);
            String categoryName = cursor.getString(1);
            cursors = db.query(idNumber, new String[]{COLUMN.Task.toString()}, null, null, null, null, null);
            while (cursors.moveToNext()) {
                String allTasks = cursors.getString(0);
                jsonArray.put(allTasks);
                add_task.add(allTasks);
            }
            jsonObject.put(idNumber,jsonArray);
            add_tasks.addAll(add_task);
            add_task.clear();
            map.put(categoryName, add_tasks);
        }
        System.out.println("JSON Object is:"+jsonObject);
        Log.v("JSON Object is:",""+jsonObject);
        return jsonObject;
    }

    public String idGenerator(){
        db = this.getReadableDatabase();
        long rowCount = DatabaseUtils.queryNumEntries(db,COLUMN.Category.toString());
        long count = rowCount+1;
        String id = "u_id_"+count;
        return id;
    }
    
    //Here TableName is ID Name
    public String getTableName(String tableName) {
        db = this.getReadableDatabase();
        String id = null;
        cursor = db.rawQuery("select id from " + COLUMN.Category.toString() + " where " + COLUMN.Category_Name.toString() + "='" + tableName + "'", null);
        while (cursor.moveToNext()) {
            id = cursor.getString(0);
        }
        return id;
    }

    public List<String> getAllTasks(String table_Name){
        List<String> all = new ArrayList<>();
        db = this.getReadableDatabase();
        cursor = db.query(table_Name,new String[]{COLUMN.Task.toString()},null,null,null,null,null);
        while (cursor.moveToNext()){
            all.add(cursor.getString(0));
        }
        return all;
    }

    public Set<String> getAllIds(){
        Set<String> ids = new LinkedHashSet<>();
        db = this.getReadableDatabase();
        cursor = db.query(COLUMN.Category.toString(),new String[]{COLUMN.id.toString()},null,null,null,null,null);
        while (cursor.moveToNext()){
            String tableName = cursor.getString(0);
            ids.add(tableName);
        }
        return ids;
    }

    public JSONObject getAllDatas(String table_Name) throws JSONException{
        jsonObject = new JSONObject();
        jsonArray = new JSONArray();
        db = this.getReadableDatabase();
        cursor = db.query(table_Name,new String[]{COLUMN.Task.toString(), COLUMN.Date.toString(), COLUMN.Time.toString()},null,null,null,null,null);
        while (cursor.moveToNext()){
            JSONObject jsonObject = new JSONObject();
            String task = cursor.getString(0);
            String date = cursor.getString(1);
            String time = cursor.getString(2);
            if(date!=null && time!=null){
                jsonObject.put("Task",task);
                jsonObject.put("Date",date);
                jsonObject.put("Time",time);
                jsonArray.put(jsonObject);
            }
        }
        jsonObject.put(table_Name,jsonArray);
        System.out.println("JSONObject is:"+jsonObject);
        return jsonObject;
    }
}