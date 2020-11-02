package com.myapp.reminderapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.OnTaskListener, AdapterView.OnItemSelectedListener {

    ImageButton send;
    EditText task;
    RecyclerView toDoList;
    Spinner spinner;
    List<String> allList ;
    String category_name;
    RecyclerAdapter recyclerAdapter;
    List<String> categoryList;

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

        send.setOnClickListener((View v)-> {
                String userTask = task.getText().toString();
                if(userTask.isEmpty())
                    Toast.makeText(getApplicationContext(),"Task cannot be empty",Toast.LENGTH_SHORT).show();
                else {
                    allList.add(userTask);
                    task.setText("");
                    recyclerAdapter = new RecyclerAdapter(MainActivity.this,allList,this);
                    toDoList.setAdapter(recyclerAdapter);
                    toDoList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                }
            });


        categoryList = new ArrayList<>();
        categoryList.add("Default");
        categoryList.add("Finished");
        categoryList.add("Add New Category");

        ArrayAdapter<String> category = new ArrayAdapter<String>(MainActivity.this,
                R.layout.spinner_text, categoryList);
        category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(category);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(int position) {
        String result = allList.get(position);
        Intent intent = new Intent(this,UserTasks.class);
        intent.putExtra("Key",result);
        startActivity(intent);
        Toast.makeText(getApplicationContext(),"Result is:"+result,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items,menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
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

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog al;
        //category_name is taken as table name
        category_name = ""+parent.getItemAtPosition(position);
        if(category_name.equals("Add New Category")){
            LinearLayout layout = new LinearLayout(this);
            EditText editText = this.categoryName();
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(editText);
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage("Category name")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String categoryName = editText.getText().toString();
                            categoryList.add(categoryName);
                            layout.removeAllViews();
                        }
                    })
                    .setNegativeButton("Calcel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            layout.removeAllViews();
                        }
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
        new sql(this).showAlert("Error","No itm is selected");
    }

    public EditText categoryName(){
        EditText et = new EditText(this);
        et.setHint("Category Name");
        et.setTextSize(15);
        return et;
    }
}