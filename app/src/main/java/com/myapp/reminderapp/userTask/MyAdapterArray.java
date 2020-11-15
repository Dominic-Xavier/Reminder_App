package com.myapp.reminderapp.userTask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.myapp.reminderapp.R;
import com.myapp.reminderapp.sql.sql;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyAdapterArray extends ArrayAdapter<String> {

    Context context;
    String string[];

    public MyAdapterArray(@NonNull Context context, int textViewResourceId, @NonNull String[] objects) {
        super(context, textViewResourceId, objects);
        string = objects;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position,convertView,parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position,convertView,parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent){
        TextView label;
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.spinner_text,parent,false);
        label = row.findViewById(R.id.spinner_text);
        label.setText(string[position]);
        if(label.getText().toString().contains("Default")){
            new sql(context).showAlert("Success","Done");
        }
        return row;
    }
}
