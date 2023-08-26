package com.example.managementapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasksList;
    private Context context;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.tasksList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasksList.get(position);
        Log.d("TASK_ADAPTER", "Binding view for task: " + task.getTitle());
        holder.tvTaskTitle.setText(task.getTitle());
        holder.tvStartDate.setText("Start Date: " + task.getStartDate());
        holder.tvEndDate.setText("End Date: " + task.getEndDate());
        holder.tvProgress.setText("Progress: " + task.getProgress() + "%");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Task selectedTask = tasksList.get(pos);
                    Intent intent = new Intent(context, TaskDetailActivity.class);
                    intent.putExtra("selected_task", selectedTask);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskTitle, tvStartDate, tvEndDate, tvProgress;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvStartDate = itemView.findViewById(R.id.tvStartDate);
            tvEndDate = itemView.findViewById(R.id.tvEndDate);
            tvProgress = itemView.findViewById(R.id.tvProgress);
        }
    }

    public void updateData(List<Task> newTasks) {
        this.tasksList.clear();
        this.tasksList.addAll(newTasks);
        notifyDataSetChanged();
    }
}
