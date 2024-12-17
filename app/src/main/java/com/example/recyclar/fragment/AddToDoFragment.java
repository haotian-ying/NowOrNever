package com.example.recyclar.fragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.recyclar.R;
import com.example.recyclar.view.SharedViewModel;
import com.example.recyclar.model.ToDo;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Arrays;
import java.util.Calendar;

public class AddToDoFragment extends BottomSheetDialogFragment {
    private ToDo newTodo;
    private final int[] statusWeekArray = new int[7];
    private boolean setChecked = false;
    private boolean everyDayRepeat;
    private int reminderHour;
    private int reminderMinute;
    private SharedViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.DialogStyle);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        return localInflater.inflate(R.layout.fragment_add_todo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Arrays.fill(statusWeekArray,0);

        // 获取视图控件
        EditText etGoalName = view.findViewById(R.id.et_goal_name);
        Button btnSave = view.findViewById(R.id.btn_save_goal);
        Switch switch_reminder = view.findViewById(R.id.switch_reminder);
        Switch switch_repeat = view.findViewById(R.id.switch_repeat);
        // 初始化 viewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        Button[] goalButtons = new Button[]{
                view.findViewById(R.id.btn_goal_1),
                view.findViewById(R.id.btn_goal_2),
                view.findViewById(R.id.btn_goal_3),
                view.findViewById(R.id.btn_goal_4),
                view.findViewById(R.id.btn_goal_5),
                view.findViewById(R.id.btn_goal_6)
        };

        // 批量设置点击监听器
        for (Button button : goalButtons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button clickedButton = (Button) v;
                    String buttonText = clickedButton.getText().toString();
                    etGoalName.setText(buttonText);
                    //Toast.makeText(getContext(), "点击了 " + clickedButton.getText(), Toast.LENGTH_SHORT).show();

                }
            });
        }
        /*
        for (Button button : weekButtons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button clickedButton = (Button) v;
                    //Toast.makeText(getContext(), "点击了 " + clickedButton.getText(), Toast.LENGTH_SHORT).show();
                    if(clickedButton.isSelected()){
                        clickedButton.setSelected(false);
                    }else{
                        clickedButton.setSelected(true);
                    }
                }
            });
        }
        */

        switch_reminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setChecked = isChecked;
                if (isChecked) {
                    showTimePickerDialog();
                } else {
                    reminderHour = 8;
                    reminderMinute = 0;
                }
            }
        });

        switch_repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    everyDayRepeat = true;
                else
                    everyDayRepeat = false;
            }
        });

        // 保存按钮点击事件
        btnSave.setOnClickListener(v -> {
            String goalName = etGoalName.getText().toString().trim();
            if (!goalName.isEmpty() ) {
                // 处理保存逻辑，例如回调到主界面
                // 新增提醒事件设置
                newTodo = new ToDo(goalName,reminderHour,reminderMinute,everyDayRepeat,setChecked);
                viewModel.setNewTodo(newTodo);
                // 模拟耗时操作
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    dismiss();
                }, 100); // 100ms 延迟，确保 ViewModel 数据已处理
            } else {
                Toast.makeText(getContext(), "目标名称不能为空", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute) -> {
                    reminderHour = hourOfDay;
                    reminderMinute = minute;
                    Toast.makeText(getContext(), "提醒时间设置为: " + hourOfDay + ":" + minute, Toast.LENGTH_SHORT).show();
                },
                currentHour, currentMinute, true);

        timePickerDialog.setTitle("选择提醒时间");
        timePickerDialog.show();
    }

}
