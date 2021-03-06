   package com.myapp.reminderapp.userTask;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.myapp.reminderapp.R;
import com.myapp.reminderapp.Services.MyAlarm;
import com.myapp.reminderapp.Services.MyService;
import com.myapp.reminderapp.alertORToast.AlertOrToast;
import com.myapp.reminderapp.sql.Sql;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

  public class MainActivity extends AppCompatActivity implements RecyclerAdapter.OnTaskListener, AdapterView.OnItemSelectedListener {

    ImageButton send;
    EditText task;
    static RecyclerView toDoList;
    static TextView noResults;
    Spinner spinner;
    static List<String> allList ;
    static String category_name;
    static RecyclerAdapter recyclerAdapter;
    List<String> categoryList;
    static int positions;
    Sql s = new Sql(this);
    Handler handler;
    SwipeRefreshLayout refreshLayout;
    static String tablename;
    Intent serviceIntent;

    public static String getTablename() {
        return tablename;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_assign_task);
        task = findViewById(R.id.task);
        send = findViewById(R.id.send);
        toDoList = findViewById(R.id.displayTask);
        spinner = findViewById(R.id.category);
        refreshLayout = findViewById(R.id.swiperefresh);
        allList = new ArrayList<>();
        noResults = findViewById(R.id.no_results);
        //linearLayout = findViewById(R.id.empty);

        serviceIntent = new Intent(getApplicationContext(), MyService.class);

        Toolbar toolbar = findViewById(R.id.custom_menu_toolBar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        categoryList = new ArrayList<>();
        categoryList.add("Select Category");
        categoryList.add("Add New Category");

        ArrayAdapter<String> category = new ArrayAdapter<String>(MainActivity.this,
                R.layout.spinner_text_view, categoryList);
        spinner.setAdapter(category);
        spinner.setOnItemSelectedListener(this);

        handler = new Handler();
        handler.postAtFrontOfQueue(()-> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                if(MyService.isServicestarted()){
                    System.out.println("Service is started:"+MyService.isServicestarted());
                    ContextCompat.startForegroundService(this, serviceIntent);
                }
                else {
                    System.err.println("Service is already running");
                }
            }
            else{
                System.out.println("Service2 is started:"+MyService.isServicestarted());
                startService(serviceIntent);
            }

            List<String> ls = s.getCategory();
            categoryList.clear();
            categoryList.add("Select Category");
            categoryList.add("Add New Category");
            for(String a:ls)
                categoryList.add(a);
        });

        send.setOnClickListener((v)-> {
                String userTask = task.getText().toString();
                if(userTask.isEmpty() || category_name.equals("Select Category"))
                    new AlertOrToast(this).toastMessage("Task is Empty or Category is not selected...!");
                else {
                    String tableIdName = s.getTableName(category_name);
                    boolean all = s.addData(tableIdName,userTask);
                    if(all){
                        allList.add(userTask);
                        task.setText("");
                        setRecyclerAdapter(MainActivity.this,allList,this);
                    }
                }
            });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!category_name.equals("Select Category") && !category_name.equals("Add New Category")){
                    String tablename = s.getTableName(category_name);
                    allList = s.getAllTasks(tablename);
                    toDoList.setVisibility(View.VISIBLE);
                    noResults.setVisibility(View.GONE);
                    setRecyclerAdapter(MainActivity.this, allList,MainActivity.this::onClick);
                    refreshLayout.setRefreshing(false);
                }
                refreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void onClick(int position) {
        positions = position;
        String result = allList.get(positions);
        Intent intent = new Intent(this, UserTasks.class);
        intent.putExtra("Task",result);
        intent.putExtra("indexPosition",String.valueOf(positions+1));
        intent.putExtra("CategoryName",getCategory_name());

        startActivity(intent);
        new AlertOrToast(this).toastMessage(intent.getStringExtra("Key"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items,menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setBackgroundColor(Color.TRANSPARENT);
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
                else{
                    toDoList.setVisibility(View.GONE);
                    noResults.setVisibility(View.VISIBLE);
                }
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
            noResults.setVisibility(View.GONE);
            allList.clear();
            setRecyclerAdapter(MainActivity.this, allList,this);
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
                        categoryList.add("Select Category");
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

        else if(!category_name.equals("Select Category")){
            tablename = s.getTableName(category_name);
            allList = s.getAllTasks(tablename);
            setRecyclerAdapter(MainActivity.this, allList,this);
        }
        else if(category_name.equals("Select Category")){
            allList.clear();
            toDoList.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
            noResults.setHint("Select Category");
        }
        else{
            allList.clear();
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

    public static void setRecyclerAdapter(Context context, List<String> allTasks, RecyclerAdapter.OnTaskListener listener){
        if(allList.isEmpty() && !category_name.equals("Select Category")){
            toDoList.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
            noResults.setHint("No Data Available");
        }
        else {
            noResults.setVisibility(View.GONE);
            toDoList.setVisibility(View.VISIBLE);
            recyclerAdapter = new RecyclerAdapter(context,allTasks,listener);
            toDoList.setAdapter(recyclerAdapter);
            toDoList.setLayoutManager(new LinearLayoutManager(context));
        }

    }
}