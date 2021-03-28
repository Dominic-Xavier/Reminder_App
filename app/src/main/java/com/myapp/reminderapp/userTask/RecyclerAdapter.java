package com.myapp.reminderapp.userTask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myapp.reminderapp.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Viewholder> implements Filterable {
    Context context;
    List<String> allList;
    List<String> allTaskList;

    private OnTaskListener listener;
    public RecyclerAdapter(Context context, List<String> allList, OnTaskListener listener) {
        this.context = context;
        this.allList = allList;
        this.listener = listener;
        allTaskList = new ArrayList<>(allList);
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
        holder.task.setText(""+allList.get(position));
        holder.check.setOnClickListener((v)-> {
                if(holder.check.isChecked()){
                    allList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeRemoved(position,allList.size());
                    String sqla = "detete from ";
                }
        });
    }

    @Override
    public int getItemCount() {
        return allList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        //Runs on background Thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList = new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredList.addAll(allTaskList);
            }
            else{
                for (String filteredItem: allTaskList) {
                    if(filteredItem.toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(filteredItem);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        //Runs on UI Thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            allList.clear();
            allList.addAll((Collection<? extends String>) results.values);
            notifyDataSetChanged();
        }
    };

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
    public interface OnTaskListener{
        void onClick(int position);
    }
}


