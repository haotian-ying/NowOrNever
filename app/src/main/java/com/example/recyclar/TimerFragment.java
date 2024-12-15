package com.example.recyclar;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerFragment extends Fragment {
    private final List<Pomodoro> PomodoroList = new ArrayList<Pomodoro>();
    private View view;//定义view用来设置fragment的layout
    private PomodoroAdapter PomodoroAdapter;
    public RecyclerView rvPomodoro;//定义RecyclerView

    public TimerFragment() {

    }

    public static TimerFragment newInstance(String param1, String param2) {
        TimerFragment fragment = new TimerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_timer, container, false);
        initPomodoro();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        PomodoroAdapter adapter = new PomodoroAdapter(PomodoroList);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void initPomodoro() {
        PomodoroList.add(new Pomodoro("待办是指您要专注的事", 1));
        PomodoroList.add(new Pomodoro("右上角+号添加待办", 10));
        PomodoroList.add(new Pomodoro("长按待办编辑或删除", 25));
        PomodoroList.add(new Pomodoro("点击开始按钮来专注计时", 1));
        PomodoroList.add(new Pomodoro("as", 60));
        PomodoroList.add(new Pomodoro("1", 90));
    }

    private void initRecyclerView() {
        //获取RecyclerView
        rvPomodoro = (RecyclerView)view.findViewById(R.id.rv);
        //创建adapter
        PomodoroAdapter = new PomodoroAdapter(PomodoroList);
        //

        rvPomodoro.setAdapter(PomodoroAdapter);
        //设置layoutManager,可以设置显示效果，是线性布局、grid布局，还是瀑布流布局
        //参数是：上下文、列表方向（横向还是纵向）、是否倒叙
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvPomodoro.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //设置item的分割线
        rvPomodoro.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

    }
}