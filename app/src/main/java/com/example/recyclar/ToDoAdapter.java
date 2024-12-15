package com.example.recyclar;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder>{
    // 保存每日对应todo项
    private final List<EveryToDo> mToDoList;
    private OnToDoItemChangeListener listener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View ToDoView;
        TextView tvTitle;
        RadioButton radioButton;
        ImageButton btnDelete;

        public ViewHolder(View view) {
            super(view);
            ToDoView = view;
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            radioButton = (RadioButton)view.findViewById(R.id.radioButton);
            btnDelete = (ImageButton)view.findViewById(R.id.btnDelete);
        }
    }

    public ToDoAdapter(List<EveryToDo> todoList) {
        mToDoList = todoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.ToDoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                EveryToDo matters = mToDoList.get(position);
                Toast.makeText(v.getContext(), "you clicked view " +  String.valueOf(matters.getId()), Toast.LENGTH_SHORT).show();
            }
        });
        /*
        holder.radioButton.setOnClickListener(v -> {
            boolean isChecked = holder.radioButton.isChecked();
        });
        */
        
        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                EveryToDo matters = mToDoList.get(position);
                Toast.makeText(v.getContext(), "you clicked todo " + String.valueOf(matters.isDone()), Toast.LENGTH_SHORT).show();
            }
        });

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                EveryToDo matters = mToDoList.get(position);
                matters.toggleDoneStatus();
                // 切换完成状态
                if (listener != null) {
                    listener.onStatusChanged(matters, matters.isDone());
                }

                // 刷新当前条目，避免状态错乱
                notifyItemChanged(position);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                EveryToDo matters = mToDoList.get(position);
                if (listener != null) {
                    listener.onItemDeleted(matters);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EveryToDo matters = mToDoList.get(position);
        holder.tvTitle.setText(matters.getTitle());
        holder.radioButton.setChecked(false); // 确保状态与数据一致
        if (matters.isDone()) {
            holder.tvTitle.setTextColor(Color.GRAY); // 文本颜色设置为灰色
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // 添加删除线
        } else {
            holder.tvTitle.setTextColor(Color.BLACK); // 文本颜色恢复为黑色
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)); // 移除删除线
        }

        // 防止状态错乱
        holder.radioButton.setOnCheckedChangeListener(null);
        holder.radioButton.setChecked(false);
    }
    //
    public interface OnToDoItemChangeListener {
        void onStatusChanged(EveryToDo item, boolean isCompleted);
        void onItemDeleted(EveryToDo item);
    }
    public void setOnToDoItemChangeListener(OnToDoItemChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return mToDoList.size();
    }


}
