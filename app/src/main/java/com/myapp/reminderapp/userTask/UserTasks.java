package com.myapp.reminderapp.userTask;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.reminderapp.R;
import com.myapp.reminderapp.alertORToast.AlertOrToast;
import com.myapp.reminderapp.sql.sql;

import java.text.DateFormat;
import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class UserTasks extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    EditText date,time,taskName;
    Button save;
    TextView task_name;
    int hour, minutes;
    sql s = new sql(this);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_timings);

        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        taskName = findViewById(R.id.text_receive);
        save = findViewById(R.id.save);
        task_name = findViewById(R.id.task_Display);
        Intent task = getIntent();
        String tasks = task.getStringExtra("Task");
        String categoryName = task.getStringExtra("CategoryName");
        new AlertOrToast(this).toastMessage("Category Name:"+categoryName);

        task_name.setText(tasks);

        date.setOnClickListener((View v)-> {
            DialogFragment datepicker = new DateDialogue();
            datepicker.show(getSupportFragmentManager(),"Date Picker");
        });

        time.setOnClickListener((View v)-> {

            TimePickerDialog dialog = new TimePickerDialog(
                    UserTasks.this,
                    (view,hourOfDay, minute) ->{
                            hour = hourOfDay;
                            minutes = minute;
                            Calendar calendar =  Calendar.getInstance();
                            calendar.set(0,0,0,hour,minutes);
                            time.setText(android.text.format.DateFormat.format("H:MM", calendar));
                    },12,0,false
                    );
            dialog.updateTime(hour,minutes);
            dialog.show();
        });

        save.setOnClickListener((v)-> {
            String remind_date = date.getText().toString();
            String remind_time = time.getText().toString();
            if(remind_date.isEmpty() || remind_time.isEmpty())
                Toast.makeText(this,"Please fill date and time",Toast.LENGTH_SHORT).show();
            else{
                String Task = task_name.getText().toString();
                String dateRemind = date.getText().toString();
                String timeRemind = time.getText().toString();
                String tableName = s.getTableName(categoryName);
                s.updateData(tableName,Task,dateRemind,timeRemind);
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        date.setText(currentDate);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
