package com.myapp.reminderapp.userTask;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.myapp.reminderapp.R;
import com.myapp.reminderapp.alertORToast.AlertOrToast;
import com.myapp.reminderapp.sql.Sql;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class UserTasks extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    EditText date,time,taskName;
    ImageButton update_Task;
    Button save;
    TextView task_name;
    Spinner spinner;
    int hour, minutes;
    Sql s = new Sql(this);
    static String repeat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_timings);

        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        taskName = findViewById(R.id.text_receive);
        save = findViewById(R.id.save);
        task_name = findViewById(R.id.task_Display);
        update_Task = findViewById(R.id.update_task);
        spinner = findViewById(R.id.repeat);
        Intent task = getIntent();
        String tasks = task.getStringExtra("Task");
        String categoryName = task.getStringExtra("CategoryName");
        new AlertOrToast(this).toastMessage("Category Name:"+categoryName);

        task_name.setText(tasks);

        update_Task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableid = s.getTableName(categoryName);
                String updateTask = taskName.getText().toString();
                String taskNames = task_name.getText().toString();
                int save = s.updateTask(tableid,taskNames,updateTask);
                if(save>0){
                    task_name.setText(updateTask);
                    taskName.setText("");
                }
            }
        });

        String repeat[] = getResources().getStringArray(R.array.Repeat_Array);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(UserTasks.this,R.layout.spinner_text,repeat);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        date.setOnClickListener((View v)-> {
            DialogFragment datepicker = new DateDialogue();
            datepicker.show(getSupportFragmentManager(),"Date Picker");
        });

        time.setOnClickListener((View v)-> {
            /*TimePickerDialog dialog1 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                }
            },12,0,false);*/
            TimePickerDialog dialog = new TimePickerDialog(UserTasks.this,
                    (view,hourOfDay, minute) ->{
                            hour = hourOfDay;
                            minutes = minute;
                            time.setText(hour+":"+minutes);
                    },hour,minutes,false);
            dialog.updateTime(hour,minutes);
            dialog.show();
        });

        save.setOnClickListener((v)-> {
            String remind_date = date.getText().toString();
            String remind_time = time.getText().toString();
            if(remind_date.isEmpty() || remind_time.isEmpty())
                Toast.makeText(this,"Please fill date and time",Toast.LENGTH_SHORT).show();
            else {
                String dateRemind = date.getText().toString();
                String timeRemind = time.getText().toString();
                String tableName = s.getTableName(categoryName);
                s.updateData(tableName,tasks,dateRemind,timeRemind);
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, dayOfMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YY");
        String currentDate = dateFormat.format(calendar.getTime());
        date.setText(currentDate);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p>
     * Implementers can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        repeat = ""+parent.getItemAtPosition(position);
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
