package com.example.recyclar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskDetailFragment extends Fragment {

    private static final String ARG_TASK = "task";
    private Task task;

    public TaskDetailFragment() {
    }

    public static TaskDetailFragment newInstance(Task task) {
        TaskDetailFragment fragment = new TaskDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TASK, task); // 传递Task对象
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            task = (Task) getArguments().getSerializable(ARG_TASK);
        }
    }

    // 保证view完全加载
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_detail, container, false);

        // 显示任务详情
        TextView tvTitle = view.findViewById(R.id.tv_task_title);
        TextView tvDescription = view.findViewById(R.id.tv_task_description);
        TextView tvPriority = view.findViewById(R.id.tv_task_priority);
        TextView tvDate = view.findViewById(R.id.tv_task_date);

        if (task != null) {
            tvTitle.setText(task.getTitle());
            tvDescription.setText(task.getDescription());
            tvPriority.setText(String.valueOf(task.getPriority()));
            tvDate.setText(task.getDate());
        }

        return view;
    }
}
