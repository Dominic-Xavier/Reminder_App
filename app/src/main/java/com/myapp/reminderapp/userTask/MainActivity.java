package com.myapp.reminderapp.userTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.myapp.reminderapp.R;
import com.myapp.reminderapp.alertORToast.AlertOrToast;
import com.myapp.reminderapp.sql.sql;

import java.util.ArrayList;
import java.util.List;

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

        /*Handler handler = new Handler();
        handler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                List<String> l = s.getTasks();
                recyclerAdapter = new RecyclerAdapter(MainActivity.this,l,MainActivity.this);
                toDoList.setAdapter(recyclerAdapter);
                toDoList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            }
        });*/

        send.setOnClickListener((View v)-> {
                String userTask = task.getText().toString();
                if(userTask.isEmpty())
                    new AlertOrToast(this).toastMessage("Task cannot be empty");
                else {
                    s.addData(category_name,userTask);
                    allList.add(userTask);
                    task.setText("");
                    recyclerAdapter = new RecyclerAdapter(MainActivity.this,allList,this);
                    toDoList.setAdapter(recyclerAdapter);
                    toDoList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                }
            });
        categoryList = new ArrayList<>();
        categoryList.add("Default");
        categoryList.add("Add New Category");
        categoryList.add("Finished");

       ArrayAdapter<String> category = new ArrayAdapter<String>(MainActivity.this,
                R.layout.spinner_text, categoryList);
        spinner.setAdapter(category);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(int position) {
        positions = position;
        String result = allList.get(positions);
        Intent intent = new Intent(this, UserTasks.class);
        intent.putExtra("Key",result);
        intent.putExtra("indexPosition",positions);

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog al;
        //category_name is taken as table name
        category_name = "" + parent.getItemAtPosition(position);
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
                        List<String> ls = s.addCategory(categoryName);
                        for (String l:ls)
                            categoryList.add(l);
                        layout.removeAllViews();
                    })
                    .setNegativeButton("Calcel", (dialog, which) -> {
                        layout.removeAllViews();
                    });
            al = builder.create();
            al.setView(layout);
            al.setCancelable(false);
            al.setCanceledOnTouchOutside(false);
            al.show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        new AlertOrToast(this).showAlert("Error","No itm is selected");
    }

    public EditText categoryName(){
        EditText et = new EditText(this);
        et.setHint("Category Name");
        et.setTextSize(15);
        return et;
    }
}