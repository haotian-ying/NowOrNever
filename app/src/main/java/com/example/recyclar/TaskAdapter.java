package com.example.recyclar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>{
    private final List<Task> mTaskList;
    private OnTaskClickListener mListener;  // Declare the listener interface

    public TaskAdapter(List<Task> taskList) {
        this.mTaskList = taskList;
    }

    // Define the OnTaskClickListener interface
    public interface OnTaskClickListener {
        void onTaskClick(Task task, int position);
    }

    // Allow setting the listener from outside
    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.mListener = listener;
    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        View TaskView;
        TextView tvTitle;
        TextView tvComment;
        TextView tvTime;

        public ViewHolder(View view) {
            super(view);
            TaskView = view;
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvComment = (TextView) view.findViewById(R.id.tvComment);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.TaskView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && mListener != null) {
                    Task task = mTaskList.get(position);
                    mListener.onTaskClick(task, position);    // Notify listener when clicked
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = mTaskList.get(position);
        holder.tvTitle.setText(task.getTitle());
        holder.tvComment.setText(task.getDescription());
        holder.tvTime.setText(String.valueOf(task.getDate()));
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

}
