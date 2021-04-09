package com.myapp.reminderapp.userTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.myapp.reminderapp.R;
import com.myapp.reminderapp.alertORToast.AlertOrToast;
import com.myapp.reminderapp.sql.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.OnTaskListener, AdapterView.OnItemSelectedListener {

    ImageButton send;
    EditText task;
    static RecyclerView toDoList;
    Spinner spinner;
    List<String> allList ;
    static String category_name;
    static RecyclerAdapter recyclerAdapter;
    List<String> categoryList;
    sql s = new sql(this);
    static int positions;
    Handler handler;
    static List<String> Tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_assign_task);
        task = findViewById(R.id.task);
        send = findViewById(R.id.send);
        toDoList = findViewById(R.id.displayTask);
        spinner = findViewById(R.id.category);
        allList = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.custom_menu_toolBar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        handler = new Handler();
        handler.postAtFrontOfQueue(()-> {
            List<String>ls = s.getCategory();
            categoryList.clear();
            categoryList.add("Select");
            categoryList.add("Add New Category");
            for(String a:ls)
                categoryList.add(a);
        });

        send.setOnClickListener((View v)-> {
                String userTask = task.getText().toString();
                if(userTask.isEmpty() || category_name.equals("Select"))
                    new AlertOrToast(this).toastMessage("Task is Empty or Category is not selected...!");
                else {
                    String tableIdName = s.getTableName(category_name);
                    s.addData(tableIdName,userTask);
                    allList.add(userTask);
                    task.setText("");
                    setRecyclerAdapter(MainActivity.this,allList,this);
                }
            });
        categoryList = new ArrayList<>();
        categoryList.add("Select");
        categoryList.add("Add New Category");

       ArrayAdapter<String> category = new ArrayAdapter<String>(MainActivity.this,
                R.layout.spinner_text_view, categoryList);
        spinner.setAdapter(category);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(int position) {
        positions = position;
        String result = allList.get(positions);
        Intent intent = new Intent(this, UserTasks.class);
        intent.putExtra("Task",result);
        intent.putExtra("indexPosition",positions);
        intent.putExtra("CategoryName",getCategory_name());
        startActivity(intent);
        new AlertOrToast(this).toastMessage(intent.getStringExtra("Key"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items,menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setBackgroundColor(Color.WHITE);
        searchView.setQueryHint("Search Task Here");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!allList.isEmpty())
                    recyclerAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name){
        this.category_name = category_name;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog al;
        //category_name is taken as table name
        category_name = "" + parent.getItemAtPosition(position);
        setCategory_name(category_name);
        if (category_name.equals("Add New Category")) {
            LinearLayout layout = new LinearLayout(this);
            EditText editText = this.categoryName();
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(editText);
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage("Category name")
                    .setPositiveButton("ok", (DialogInterface dialog, int which) -> {
                        //categoryName is Table Name
                        String categoryName = editText.getText().toString();
                        List<String>ls = s.addCategory(categoryName);
                        categoryList.clear();
                        categoryList.add("Select");
                        categoryList.add("Add New Category");
                        for (String l:ls)
                            categoryList.add(l);
                        layout.removeAllViews();
                    })
                    .setNegativeButton("Calcel", (dialog, which) -> {
                        layout.removeAllViews();
                        dialog.dismiss();
                    });
            al = builder.create();
            al.setView(layout);
            al.setCancelable(false);
            al.setCanceledOnTouchOutside(false);
            al.show();
        }
        else if(!category_name.equals("Select")){
            String tablename = s.getTableName(category_name);
            allList = s.getAllTasks(tablename);
            setRecyclerAdapter(MainActivity.this, allList,this);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        new AlertOrToast(this).showAlert("Error","No itm is selected");
    }

    public EditText categoryName(){
        EditText et = new EditText(this);
        et.setHint("Category Name");
        et.setTextSize(20);
        return et;
    }

    public TextView emptyText(Context context){
        TextView tv = new TextView(context);
        tv.setText("No Results");
        tv.setTypeface(null,Typeface.BOLD);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(20);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    public static void setRecyclerAdapter(Context context, List<String> allTasks, RecyclerAdapter.OnTaskListener listener){
        recyclerAdapter = new RecyclerAdapter(context,allTasks,listener);
        toDoList.setAdapter(recyclerAdapter);
        toDoList.setLayoutManager(new LinearLayoutManager(context));
    }
}