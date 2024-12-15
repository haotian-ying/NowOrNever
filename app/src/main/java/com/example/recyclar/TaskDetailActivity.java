package com.example.recyclar;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TaskDetailActivity extends AppCompatActivity{
    String[] priorityText= new String[]{"重要且紧急","紧急不重要","重要不紧急","不紧急不重要"};
    String[] statusText= new String[]{"待开始","正在进行","已完成"};
    private final int STATUS_EDIT = 1;
    private final int STATUS_DELETE = 2;
    private TextView tvTaskTitle;
    private TextView tvTaskDescription;
    private TextView tvTaskPriority;
    private TextView tvTaskDate;
    private TextView tvTaskStatus;
    private TextView tvEditTask;
    private TextView tvDeleteTask;
    private ImageView ivBack;
    private Button btnConfirm;

    private static String title;
    private static String description;
    private static String priority;
    private static String time;
    private static String status;

    private static String updatedTitle;
    private static String updatedDescription;
    private static String updatedPriority;
    private static String updatedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        tvTaskTitle = findViewById(R.id.tvTaskTitle);
        tvTaskDescription = findViewById(R.id.tvTaskDescription);
        tvTaskPriority = findViewById(R.id.tvTaskPriority);
        tvTaskDate = findViewById(R.id.tvTaskDate);
        tvTaskStatus = findViewById(R.id.tvTaskStatus);
        tvEditTask = findViewById(R.id.tvEditTask);
        tvDeleteTask = findViewById(R.id.tvDeleteTask);
        btnConfirm = findViewById(R.id.btnConfirm);
        ivBack = findViewById(R.id.ivBack);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");
        priority = intent.getStringExtra("priority");
        time = intent.getStringExtra("time");
        status = intent.getStringExtra("status");

        tvTaskTitle.setText(title);
        tvTaskDescription.setText(description);
        tvTaskPriority.setText(priorityText[Integer.parseInt(priority)]);
        tvTaskDate.setText(time);
        tvTaskStatus.setText(statusText[Integer.parseInt(status)]);

        // TODO:修改逻辑
        tvEditTask.setOnClickListener(v -> {
            // Modify the task, then return the updated task
            showBottomSheetDialog();
        });


        tvDeleteTask.setOnClickListener(v -> {
            // Return a signal to delete the task
            Intent resultIntent = new Intent();
            resultIntent.putExtra("operation",STATUS_DELETE);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        btnConfirm.setOnClickListener(v->{
            Intent resultIntent = new Intent();
            System.out.println(title);
            resultIntent.putExtra("operation",STATUS_EDIT);
            resultIntent.putExtra("title", updatedTitle);
            resultIntent.putExtra("comment", updatedDescription);
            resultIntent.putExtra("priority", updatedPriority); // Updated priority
            resultIntent.putExtra("time", updatedTime); // Updated date
            resultIntent.putExtra("status", status); // Updated date

            setResult(RESULT_OK, resultIntent);
            finish(); // Finish the activity and return to HomeFragment
        });

        ivBack.setOnClickListener(v->{
            finish();
        });
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this,R.style.BottomSheetStyle);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);

        TextView tv_title = bottomSheetView.findViewById(R.id.tv_title);
        EditText etTaskTitle = bottomSheetView.findViewById(R.id.et_task_title);
        EditText etTaskDescription = bottomSheetView.findViewById(R.id.et_task_description);
        EditText etTaskPriority = bottomSheetView.findViewById(R.id.et_task_priority);
        Button btnPickDate = bottomSheetView.findViewById(R.id.btn_pick_date);
        Button btnSubmit = bottomSheetView.findViewById(R.id.btn_submit);
        Button btnCancel = bottomSheetView.findViewById(R.id.btn_cancel);

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        tv_title.setText("修改任务");
        etTaskTitle.setHint(title);
        etTaskDescription.setHint(description);
        etTaskPriority.setHint(priority);

        // 日期选择
        final Calendar calendar = Calendar.getInstance();
        btnPickDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                btnPickDate.setText(String.format("%d-%02d-%02d", year, month + 1, dayOfMonth));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // 提交任务
        btnSubmit.setOnClickListener(v -> {
            updatedTitle = etTaskTitle.getText().toString();
            System.out.println("sheet" + title);
            updatedDescription = etTaskDescription.getText().toString();
            updatedPriority = etTaskPriority.getText().toString();
            //priority = Integer.parseInt(etTaskPriority.getText().toString());
            Date date = calendar.getTime();
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
            updatedTime = sDateFormat.format(date);
            if (title.isEmpty() || description.isEmpty() || priority.isEmpty()) {
                Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            } else {
                tvTaskTitle.setText(updatedTitle);
                tvTaskDescription.setText(updatedDescription);
                tvTaskPriority.setText(priorityText[Integer.parseInt(updatedPriority)]);
                tvTaskDate.setText(updatedTime);
                if (!isFinishing()) { // 确保 Activity 没有被销毁
                    bottomSheetDialog.dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });
    }
}