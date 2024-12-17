package com.example.recyclar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclar.model.Pomodoro;
import com.example.recyclar.R;

import java.util.List;

public class PomodoroAdapter  extends RecyclerView.Adapter<PomodoroAdapter.ViewHolder>{
    private final List<Pomodoro> mPomodoroList;
    private OnItemClickListener mListener;
    private OnItemLongClickListener mLongClickListener;
    private int[] ImageArray = {R.drawable.pomodoro0,R.drawable.pomodoro1,
            R.drawable.pomodoro2,R.drawable.pomodoro3,R.drawable.pomodoro4,R.drawable.pomodoro5};
    public interface OnItemClickListener {
        void onItemClick(Pomodoro pomodoro); // 点击事件方法
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position); // 长按事件，传递子项位置
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mLongClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View PomodoroView;
        TextView PomodoroName;
        TextView PomodoroTime;
        ImageView ivPomodoro;

        public ViewHolder(View view) {
            super(view);
            PomodoroView = view;
            PomodoroName = (TextView) view.findViewById(R.id.tvName);
            PomodoroTime = (TextView) view.findViewById(R.id.tvTime);
            ivPomodoro = (ImageView) view.findViewById(R.id.ivPomodoro);
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
        // 设置点击监听
        holder.PomodoroView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            if (mListener != null && position != RecyclerView.NO_POSITION) {
                mListener.onItemClick(mPomodoroList.get(position));
            }
        });
        /*
        holder.PomodoroName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Pomodoro matters = mPomodoroList.get(position);
                Toast.makeText(v.getContext(), "you clicked todo " + matters.getMatters(), Toast.LENGTH_SHORT).show();
            }
        });
        */
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PomodoroAdapter.ViewHolder holder, int position) {
        Pomodoro matters = mPomodoroList.get(position);
        holder.PomodoroName.setText(matters.getMatters());
        holder.PomodoroTime.setText(String.valueOf(matters.getTime()));

        int randomnumber = (int)(Math.random()*5) ;
        holder.ivPomodoro.setImageResource(ImageArray[randomnumber]);
    }

    @Override
    public int getItemCount() {
        return mPomodoroList.size();
    }

    public void removeItem(int position) {
        mPomodoroList.remove(position);
        notifyItemRemoved(position);
    }
}
