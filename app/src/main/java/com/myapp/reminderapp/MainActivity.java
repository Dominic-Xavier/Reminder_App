package com.myapp.reminderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskDisplay.OnTaskListener, AdapterView.OnItemSelectedListener {

    ImageButton send;
    EditText task;
    RecyclerView toDoList;
    Spinner spinner;
    List<String> allList = new ArrayList<>();
    String category_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_assign_task);
        task = findViewById(R.id.task);
        send = findViewById(R.id.send);
        toDoList = findViewById(R.id.displayTask);
        spinner = findViewById(R.id.category);

        TaskDisplay ts = new TaskDisplay(MainActivity.this,allList,this);
        toDoList.setAdapter(ts);
        toDoList.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        send.setOnClickListener((View v)-> {
                String userTask = task.getText().toString();
                if(userTask.isEmpty())
                    Toast.makeText(getApplicationContext(),"Task cannot be empty",Toast.LENGTH_SHORT).show();
                else {
                    allList.add(userTask);
                    ts.notifyItemInserted(allList.size());
                    task.setText("");
                }
            });

        List<String> list = new ArrayList<>();
        list.add("Default");
        list.add("Finished");

        ArrayAdapter<String> category = new ArrayAdapter<String>(MainActivity.this,
                R.layout.spinner_text, list);
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.search){
            SearchView search = (SearchView) item.getActionView();
            search.setQueryHint("Search Task !!");
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category_name = ""+parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        new sql(this).showAlert("Error","No itm is selected");
    }
}