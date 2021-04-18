package com.myapp.reminderapp.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.myapp.reminderapp.alertORToast.AlertOrToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.Nullable;

public class Sql extends SQLiteOpenHelper {
    private static SQLiteDatabase db;
    Context context;
    ContentValues cv;
    static Cursor cursor, cursors;
    static Map<String, String> maps;
    JSONArray jsonArray = new JSONArray();

    public Sql(@Nullable Context context) {
        super(context, "category.db", null, 1);
        this.context = context;
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
            db.execSQL("create table u_id_1" + " (" + COLUMN.Task.toString()+" text," + COLUMN.Date.toString() + " text," + COLUMN.Time.toString() + " text)");
            db.execSQL("create table u_id_2" + " (" + COLUMN.Task.toString()+" text," + COLUMN.Date.toString() + " text," + COLUMN.Time.toString() + " text)");
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
                db.execSQL("create table "+ID_NUMBER+" (" + COLUMN.id.toString() + " INTEGER PRIMARY KEY AUTOINCREMENT,"+ COLUMN.Task.toString() + " text," + COLUMN.Date.toString() + " text," + COLUMN.Time.toString() + " text)");
            } else
                new AlertOrToast(context).showAlert("Faled", "Category is not added into database");
        } else
            new AlertOrToast(context).toastMessage("This Category already exists");
        return getCategory();
    }

    public boolean addData(String categoryName, String Task) {
        long save;
        boolean duplicateTask = false;
        //here categoryName is table name
        if(checkDuplicateTask(categoryName,Task)){
            db = this.getWritableDatabase();
            cv = new ContentValues();
            cv.put(COLUMN.Task.toString(), Task);
            save = db.insert(categoryName, null, cv);
            if (save == -1)
                new AlertOrToast(context).showAlert("Failed", "Error Occured");
            else{
                duplicateTask = true;
                new AlertOrToast(context).toastMessage("Values entered successfully...!");
            }
        }
        else{
            duplicateTask = false;
            new AlertOrToast(context).toastMessage("This task Exists already");
        }
        return duplicateTask;
    }

    public void updateData(String tableName, String task, String date, String time) {
        //here categoryName is table name
        db = this.getWritableDatabase();
        cv = new ContentValues();

            cv.put(COLUMN.Date.toString(), date);
            cv.put(COLUMN.Time.toString(), time);
            String where = COLUMN.Task.toString() + "=?";
            String[] whereArgs = {task};
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

    public int updateTask(String catagoryName, String task, String update_Task) {
        int l = 0;
        if(checkDuplicateTask(catagoryName,update_Task)){
            db = this.getWritableDatabase();
            cv = new ContentValues();
            cv.put(COLUMN.Task.toString(), update_Task);
            String whereClause = COLUMN.Task.toString() + "=?";
            String[] whereArgs = {task};
            l = db.update(catagoryName, cv, whereClause, whereArgs);
            if (l != 0)
                new AlertOrToast(context).showAlert("Success", "Value updated");
            else
                new AlertOrToast(context).showAlert("Error", "Error Occured"+l);
        }
        else
            new AlertOrToast(context).toastMessage("This Task Exists Already....!");
        return l;
    }

    public void deleteRow(String tableName, String task){
        System.out.println("TableName and task is:"+tableName+" "+task);
        db = this.getWritableDatabase();
        cv = new ContentValues();
        Map<String, String> map = getRowDetails(tableName, task);
        String getTask = map.get("Tasks");
        String date = map.get("Dates");
        String time = map.get("Times");
        System.out.println("Map object for delete Row is:"+map);
        String whereClause = COLUMN.Task.toString()+"=?";
        String selectionArgs[] = {task};
        int deleteRow = db.delete(tableName,whereClause,selectionArgs);

        if(deleteRow>0)
            new AlertOrToast(context).toastMessage("Task Deleted");
         else
            new AlertOrToast(context).toastMessage("Unable to delete the task");
             System.out.println("Tasks are:"+getTask);
             cv.put(COLUMN.Task.toString(), getTask);
             cv.put(COLUMN.Date.toString(), date);
             cv.put(COLUMN.Time.toString(), time);
             long save = db.insert("u_id_2",null,cv);

             if(save!=-1)
                 new AlertOrToast(context).showAlert("Success","Value inserted into finished category");
             else
                 new AlertOrToast(context).showAlert("Error", "Value is not inserted into finished Database");

    }

    public Map<String, String> getRowDetails(String tableNames, String getTask){
        db = this.getReadableDatabase();
        maps = new LinkedHashMap<>();
        String task,Date,Time;
        cursor = db.rawQuery("select * from "+tableNames+" where "+COLUMN.Task.toString()+"='"+getTask+"'",null);
        while (cursor.moveToNext()){
            task = cursor.getString(0);
            Date = cursor.getString(1);
            Time = cursor.getString(2);
            maps.put("Tasks",task);
            if(Date!=null || Time!=null){
                maps.put("Dates",Date);
                maps.put("Times",Time);
            }
            else {
                maps.put("Dates","0");
                maps.put("Times","0");
            }
        }
        return maps;
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
    public JSONArray allDatas() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        db = this.getReadableDatabase();
        cursor = db.query(COLUMN.Category.toString(), new String[]{COLUMN.id.toString(), COLUMN.Category_Name.toString()}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            //Here idNumber is TableName
            String idNumber = cursor.getString(0);
            if(!idNumber.equals("u_id_2")){
                cursors = db.query(idNumber, new String[]{COLUMN.Task.toString(), COLUMN.Date.toString(), COLUMN.Time.toString()}, null, null, null, null, null);
                while (cursors.moveToNext()) {
                    String allTasks = cursors.getString(0);
                    String Date = cursors.getString(1);
                    String Time = cursors.getString(2);
                    if(Date!=null && Time!=null){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("Task",allTasks);
                        jsonObject.put("Date",Date);
                        jsonObject.put("Time",Time);
                        jsonArray.put(jsonObject);
                    }
                }
            }
        }
        System.out.println("JSON Object is:"+jsonArray);
        return jsonArray;
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

    public JSONArray getAllDatas(String table_Name) throws JSONException{
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
        System.out.println("JSONObject is:"+jsonArray);
        return jsonArray;
    }

    public boolean checkDuplicateTask(String tableName, String task){
        boolean checkDuplicate;
        int count = 0;
        db = this.getReadableDatabase();
        String cuurentTask;
        cursor = db.query(tableName,new String[]{COLUMN.Task.toString()},null,null,null,null,null);
        while (cursor.moveToNext()){
            cuurentTask = cursor.getString(0);
            if(cuurentTask.equals(task)){
                count++;
                break;
            }
        }
        if(count>0)
            checkDuplicate = false;
        else
            checkDuplicate = true;
        return checkDuplicate;
        //return count>0? false : true;
    }
}