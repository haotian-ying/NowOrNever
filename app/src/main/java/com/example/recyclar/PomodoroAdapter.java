package com.example.recyclar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PomodoroAdapter  extends RecyclerView.Adapter<PomodoroAdapter.ViewHolder>{
    private final List<Pomodoro> mPomodoroList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View PomodoroView;
        TextView PomodoroName;
        TextView PomodoroTime;

        public ViewHolder(View view) {
            super(view);
            PomodoroView = view;
            PomodoroName = (TextView) view.findViewById(R.id.tvName);
            PomodoroTime = (TextView) view.findViewById(R.id.tvTime);
        }
    }

    public PomodoroAdapter(List<Pomodoro> pomodoroList) {
        mPomodoroList = pomodoroList;
    }

    @NonNull
    @Override
    public PomodoroAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pomodoro_item, parent, false);
        final PomodoroAdapter.ViewHolder holder = new PomodoroAdapter.ViewHolder(view);
        holder.PomodoroView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Pomodoro matters = mPomodoroList.get(position);
                Toast.makeText(v.getContext(), "you clicked view " + matters.getMatters(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.PomodoroName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Pomodoro matters = mPomodoroList.get(position);
                Toast.makeText(v.getContext(), "you clicked todo " + matters.getMatters(), Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PomodoroAdapter.ViewHolder holder, int position) {
        Pomodoro matters = mPomodoroList.get(position);
        holder.PomodoroName.setText(matters.getMatters());
        holder.PomodoroTime.setText(String.valueOf(matters.getTime()));
    }

    @Override
    public int getItemCount() {
        return mPomodoroList.size();
    }
}
