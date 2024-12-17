package com.example.recyclar.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.recyclar.model.Pomodoro;
import com.example.recyclar.adapter.PomodoroAdapter;
import com.example.recyclar.R;
import com.example.recyclar.activity.ClockActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerFragment extends Fragment {
    private final List<Pomodoro> PomodoroList = new ArrayList<Pomodoro>();
    private PomodoroAdapter pomodoroAdapter;
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
        //定义view用来设置fragment的layout
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        initPomodoro();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        recyclerView.setLayoutManager(layoutManager);
        pomodoroAdapter = new PomodoroAdapter(PomodoroList);
        recyclerView.setAdapter(pomodoroAdapter);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 2;
            }
        });

        pomodoroAdapter.setOnItemClickListener(pomodoro -> {
            // 点击子项时获取标题和时长信息
            String title = pomodoro.getMatters();
            int duration = pomodoro.getTime();
            Intent intent = new Intent(getContext(), ClockActivity.class);
            intent.putExtra("matters", title); // 传递标题
            intent.putExtra("time", duration); // 传递时间
            startActivity(intent);
            // Toast.makeText(getContext(), "标题: " + title + ", 时长: " + duration, Toast.LENGTH_SHORT).show();
        });

        // 设置长按事件监听器
        pomodoroAdapter.setOnItemLongClickListener(position -> showDeleteDialog(position, pomodoroAdapter));

        savePomodoroToPreferences(new Pomodoro("3", 25));
        loadPomodoroFromPreferences();

        // 设置 FloatingActionButton 点击事件
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> showAddPomodoroDialog());

        return view;
    }

    private void initPomodoro() {
        savePomodoroToPreferences(new Pomodoro("as", 60));
        savePomodoroToPreferences(new Pomodoro("1", 90));

//        PomodoroList.add(new Pomodoro("待办是指您要专注的事", 1));
//        PomodoroList.add(new Pomodoro("右上角+号添加待办", 10));
//        PomodoroList.add(new Pomodoro("长按待办编辑或删除", 25));
//        PomodoroList.add(new Pomodoro("点击开始按钮来专注计时", 1));
    }

    private void showAddPomodoroDialog() {
        // 创建弹窗布局
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("添加番茄钟");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        // 添加输入框：名称
        EditText nameInput = new EditText(getContext());
        nameInput.setHint("名称");
        layout.addView(nameInput);

        // 添加输入框：时长
        EditText timeInput = new EditText(getContext());
        timeInput.setHint("时长（分钟）");
        timeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(timeInput);

        builder.setView(layout);

        // 设置“保存”按钮
        builder.setPositiveButton("保存", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String timeStr = timeInput.getText().toString().trim();

            if (name.isEmpty() || timeStr.isEmpty()) {
                Toast.makeText(getContext(), "名称和时长不能为空！", Toast.LENGTH_SHORT).show();
                return;
            }

            int time = Integer.parseInt(timeStr);

            // 保存到 SharedPreferences
            Pomodoro newPomodoro = new Pomodoro(name, time);
            savePomodoroToPreferences(newPomodoro);

            // 刷新 RecyclerView
            PomodoroList.add(newPomodoro);
            pomodoroAdapter.notifyDataSetChanged();

            Toast.makeText(getContext(), "番茄钟已添加！", Toast.LENGTH_SHORT).show();
        });

        // 设置“取消”按钮
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showDeleteDialog(int position, PomodoroAdapter adapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("删除番茄钟")
                .setMessage("确定要删除此番茄钟吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    // 从列表和 SharedPreferences 中删除
                    Pomodoro pomodoro = PomodoroList.get(position);
                    deletePomodoroFromPreferences(pomodoro);
                    PomodoroList.remove(position);
                    adapter.notifyItemRemoved(position);

                    Toast.makeText(getContext(), "番茄钟已删除", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void deletePomodoroFromPreferences(Pomodoro pomodoro) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PomodoroPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(pomodoro.getMatters()); // 删除对应的键值对
        editor.apply();
    }

    private void savePomodoroToPreferences(Pomodoro p) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PomodoroPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // editor.clear(); // 清空旧数据
        editor.putInt(p.getMatters(), p.getTime());

        editor.apply();
        // Toast.makeText(getContext(), "Data saved to SharedPreferences", Toast.LENGTH_SHORT).show();
    }

    private void loadPomodoroFromPreferences() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PomodoroPrefs", Context.MODE_PRIVATE);
        PomodoroList.clear();

        // 假设需要从 SharedPreferences 加载所有键值对
        for (String key : sharedPreferences.getAll().keySet()) {
            int time = sharedPreferences.getInt(key, 0); // 默认值为 0
            PomodoroList.add(new Pomodoro(key, time));
        }
    }
}