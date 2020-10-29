package com.myapp.reminderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskDisplay extends RecyclerView.Adapter<TaskDisplay.Viewholder> {
    Context context;
    List<String> tasks;

    private OnTaskListener listener;
    public TaskDisplay(Context context, List<String> allList, OnTaskListener listener) {
        this.context = context;
        this.tasks = allList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_design,parent,false);
        Viewholder viewholder = new Viewholder(view,listener);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        holder.task.setText(""+tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {
        TextView task;
        CheckBox check;
        LinearLayout layout;
        OnTaskListener taskListener;
        public Viewholder(@NonNull View itemView, OnTaskListener taskListener) {
            super(itemView);
            task = itemView.findViewById(R.id.task_name);
            check = itemView.findViewById(R.id.check);
            layout = itemView.findViewById(R.id.linear);
            this.taskListener = taskListener;
            layout.setOnClickListener((v)-> {
                taskListener.onClick(getAdapterPosition());
            });
        }
    }

    @FunctionalInterface
    interface OnTaskListener{
        void onClick(int position);
    }
}


