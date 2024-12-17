package com.example.recyclar.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recyclar.R;
import com.example.recyclar.model.Task;
import com.example.recyclar.adapter.TaskAdapter;
import com.example.recyclar.model.TaskDatabaseHelper;
import com.example.recyclar.activity.TaskDetailActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    String[] priorityText= new String[]{"重要且紧急","紧急不重要","重要不紧急","不紧急不重要"};
    String[] statusText= new String[]{"待开始","正在进行","已完成"};
    private final List<Task> taskList = new ArrayList<Task>();
    private View view;//定义view用来设置fragment的layout
    private TaskAdapter taskAdapter;
    private RecyclerView rvTask;//定义RecyclerView
    private BottomSheetBehavior mBottomSheetBehavior; //底部bottom sheet
    private FloatingActionButton fab;
    private ProgressBar progressBar;
    private TextView tv_progress_desc;
    private TextView tv_progress_percentage;
    private TaskDatabaseHelper dbHelper;
    private ActivityResultLauncher<Intent> resultLauncher;
    private String taskItemId;
    // 今日任务情况
    private int progress = 0;
    // status对应具体状态
    private int STATUS_TODO = 0;
    private int STATUS_INPROGRESS = 1;
    private int STATUS_DONE = 2;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new TaskDatabaseHelper(getContext());

        // Register the ActivityResultLauncher for result handling
        resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Task taskToBeChanged = null;
                    // 找到待操作task
                    for (Task task : taskList) {
                        if (Objects.equals(task.getId(), taskItemId)) {
                            taskToBeChanged = task;
                            break;
                        }
                    }
                    if (data != null) {
                        int operation = data.getIntExtra("operation",2);
                        if (operation == 2) {
                            if (taskToBeChanged != null) {
                                deleteTask(taskToBeChanged); // Delete the task
                            }
                        } else if(operation == 1){
                            // Handle task updates
                            String title = data.getStringExtra("title");
                            String comment = data.getStringExtra("comment");
                            String priority = data.getStringExtra("priority");
                            String time = data.getStringExtra("time");
                            String status = data.getStringExtra("status");
                            Task taskToBeUpdated = new Task(title, comment, Integer.parseInt(priority), time, Integer.parseInt(status));
                            updateTask(taskToBeUpdated);
                        }
                    }
                }
            }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        rvTask = (RecyclerView) view.findViewById(R.id.rvTask);
        fab = view.findViewById(R.id.fab);
        progressBar = view.findViewById(R.id.progressBar);
        tv_progress_desc = view.findViewById(R.id.tv_progress_desc);
        tv_progress_percentage = view.findViewById(R.id.tv_progress_percentage);

//        bottomSheet = view.findViewById(R.id.bottom_sheet);
//        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
//        mBottomSheetBehavior.setState(STATE_COLLAPSED);
//        mBottomSheetBehavior.setPeekHeight(150);
        //initTask();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvTask.setLayoutManager(layoutManager);
        taskAdapter = new TaskAdapter(taskList);
        rvTask.setAdapter(taskAdapter);

        // Set the click listener
        taskAdapter.setOnTaskClickListener((task, position) -> {
            //showTaskDetail(task);  // Handle the click event
            taskItemId = task.getId();
            System.out.println("全局"+String.valueOf(taskItemId));
            // 获取点击位置
            Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
//            Bundle bundle = new Bundle();
            intent.putExtra("title",task.getTitle());
            intent.putExtra("description",task.getDescription());
            intent.putExtra("priority",String.valueOf(task.getPriority()));
            intent.putExtra("time",task.getDate());
            intent.putExtra("status",String.valueOf(task.getStatus()));
            // Use the result launcher to start TaskDetailActivity and handle the result
            resultLauncher.launch(intent);
        });

        // 加载数据
        loadTasksFromDatabase();
        updateProgressBar();
        fab.setOnClickListener(v -> showBottomSheetDialog());
        return view;
    }

    // 更新进度条
    private void updateProgressBar() {
        int totalTasks = taskList.size();
        if (totalTasks == 0) {
            progress = 0; // 如果没有任务，进度为0
            progressBar.setProgress(0); // 设置进度条的值
            tv_progress_desc.setText(String.valueOf(0));
            tv_progress_percentage.setText(String.valueOf(0));
        }

        int completedTasks = 0;
        for (Task task : taskList) {
            if (task.getStatus() == STATUS_DONE) {
                completedTasks++;
            }
        }

        progress = (int) ((completedTasks / (float) totalTasks) * 100);
        progressBar.setProgress(progress); // 设置进度条的值
        Log.d("MainActivity", "progress:" + progress);
        tv_progress_desc.setText(String.valueOf(completedTasks));
        tv_progress_percentage.setText(String.valueOf(progress));
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(),R.style.BottomSheetStyle);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);

        EditText etTaskTitle = bottomSheetView.findViewById(R.id.et_task_title);
        EditText etTaskDescription = bottomSheetView.findViewById(R.id.et_task_description);
        EditText etTaskPriority = bottomSheetView.findViewById(R.id.et_task_priority);
        Button btnPickDate = bottomSheetView.findViewById(R.id.btn_pick_date);
        Button btnSubmit = bottomSheetView.findViewById(R.id.btn_submit);
        Button btnCancel = bottomSheetView.findViewById(R.id.btn_cancel);

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        // 日期选择
        final Calendar calendar = Calendar.getInstance();
        btnPickDate.setOnClickListener(v -> {
            new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                btnPickDate.setText(String.format("%d-%02d-%02d", year, month + 1, dayOfMonth));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // 提交任务
        btnSubmit.setOnClickListener(v -> {
            String title = etTaskTitle.getText().toString();
            String description = etTaskDescription.getText().toString();
            String strPriority = etTaskPriority.getText().toString();
            int priority = -1;
            if(strPriority.length() > 0){
                priority = Integer.parseInt(strPriority);
            }
            //priority = Integer.parseInt(etTaskPriority.getText().toString());
            Date date = calendar.getTime();
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
            String time = sDateFormat.format(date);

            if (title.isEmpty() || description.isEmpty() || priority ==  -1) {
                Toast.makeText(getContext(), "请填写完整信息", Toast.LENGTH_SHORT).show();
            } else {
                Task newTask = new Task(title, description,priority,time,0);
                // 插入任务到数据库
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(TaskDatabaseHelper.COLUMN_TASK_ID, newTask.getId());
                values.put(TaskDatabaseHelper.COLUMN_TASK_TITLE, newTask.getTitle());
                values.put(TaskDatabaseHelper.COLUMN_TASK_DESCRIPTION, newTask.getDescription());
                values.put(TaskDatabaseHelper.COLUMN_TASK_PRIORITY, newTask.getPriority());
                values.put(TaskDatabaseHelper.COLUMN_TASK_DATE, newTask.getDate());
                values.put(TaskDatabaseHelper.COLUMN_TASK_STATUS, newTask.getStatus());

                long rowId = db.insert(TaskDatabaseHelper.TABLE_TASKS, null, values);

                // 如果插入成功
                if (rowId != -1) {
                    taskList.add(newTask);
                    taskAdapter.notifyItemInserted(taskList.size() - 1);
                    updateProgressBar();
                    Toast.makeText(getContext(), "任务已添加", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }
            }
        });


        btnCancel.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

    }

    private void initTask() {
        List<Task> temp =  new ArrayList<Task>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Date date = new Date();
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
        String time = sDateFormat.format(date);
        Task sample = new Task("完成ToDoApp 界面构建", "CardView与RecyclerView结合使用", 3, time,0);
        temp.add(sample);

        // Task 1: App development task
        Calendar cal1 = Calendar.getInstance();
        cal1.add(Calendar.DAY_OF_MONTH, -3);
        String pastDate1 = sDateFormat.format(cal1.getTime());
        Task task1 = new Task("完成数据库设计", "设计SQLite数据库表结构和关系", 1, pastDate1, 2);
        temp.add(task1);

        // Task 2: UI improvement task
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DAY_OF_MONTH, -2);
        String pastDate2 = sDateFormat.format(cal2.getTime());
        Task task2 = new Task("优化用户界面", "调整颜色方案和布局细节", 2, pastDate2, 1);
        temp.add(task2);

        // Task 3: Feature implementation
        Calendar cal3 = Calendar.getInstance();
        cal3.add(Calendar.DAY_OF_MONTH, -7);
        String pastDate3 = sDateFormat.format(cal3.getTime());
        Task task3 = new Task("实现搜索功能", "添加任务搜索和过滤功能", 2, pastDate3, 0);
        temp.add(task3);

        // Task 4: Documentation
        Calendar cal4 = Calendar.getInstance();
        cal4.add(Calendar.DAY_OF_MONTH, 5);
        String futureDate1 = sDateFormat.format(cal4.getTime());
        Task task4 = new Task("编写项目文档", "准备项目说明和使用指南", 3, futureDate1, 0);
        temp.add(task4);

        // Task 5: Testing
        Calendar cal5 = Calendar.getInstance();
        cal4.add(Calendar.DAY_OF_MONTH, 7);
        String futureDate2 = sDateFormat.format(cal5.getTime());
        Task task5 = new Task("进行单元测试", "对主要功能模块进行完整测试", 3, futureDate2, 0);
        temp.add(task5);

        // 使用ContentValues插入数据
        for(Task t : temp){
            ContentValues values = new ContentValues();
            values.put(TaskDatabaseHelper.COLUMN_TASK_ID, t.getId());
            values.put(TaskDatabaseHelper.COLUMN_TASK_TITLE, t.getTitle());
            values.put(TaskDatabaseHelper.COLUMN_TASK_DESCRIPTION, t.getDescription());
            values.put(TaskDatabaseHelper.COLUMN_TASK_PRIORITY, t.getPriority());
            values.put(TaskDatabaseHelper.COLUMN_TASK_DATE, t.getDate());
            values.put(TaskDatabaseHelper.COLUMN_TASK_STATUS, t.getStatus());
            // 插入到数据库
            long rowId = db.insert(TaskDatabaseHelper.TABLE_TASKS, null, values);
            // 如果插入成功，将任务添加到 taskList
            if (rowId != -1) {
                taskList.add(t);
            }
        }
    }

    private void deleteTask(Task task) {
        taskList.remove(task); // Remove task from the list
        taskAdapter.notifyDataSetChanged(); // Notify adapter to update UI
        System.out.println(task.getId());
        // Remove task from the database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = TaskDatabaseHelper.COLUMN_TASK_ID + " = ?";
        String[] whereArgs = {String.valueOf(taskItemId)};
        updateProgressBar();
        db.delete(TaskDatabaseHelper.TABLE_TASKS, whereClause, whereArgs);
        db.close();
    }

    private void updateTask(Task taskToBeUpdated) {
        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            if (Objects.equals(task.getId(), taskItemId)){
                taskList.set(i,taskToBeUpdated);
                break;
            }
        }
        taskAdapter.notifyDataSetChanged();  // Update the RecyclerView

        // 更新数据库中
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskDatabaseHelper.COLUMN_TASK_TITLE, taskToBeUpdated.getTitle());
        values.put(TaskDatabaseHelper.COLUMN_TASK_DESCRIPTION, taskToBeUpdated.getDescription());
        values.put(TaskDatabaseHelper.COLUMN_TASK_PRIORITY, taskToBeUpdated.getPriority());
        values.put(TaskDatabaseHelper.COLUMN_TASK_DATE, taskToBeUpdated.getDate());
        values.put(TaskDatabaseHelper.COLUMN_TASK_STATUS, taskToBeUpdated.getStatus());

        String whereClause = TaskDatabaseHelper.COLUMN_TASK_ID + " = ?";
        String[] whereArgs = {String.valueOf(taskItemId)};

        updateProgressBar();

        db.update(TaskDatabaseHelper.TABLE_TASKS, values, whereClause, whereArgs);
        db.close();
    }

    private void loadTasksFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskDatabaseHelper.TABLE_TASKS, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_TASK_ID));
//            System.out.println("database"+id);
            String title = cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_TASK_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_TASK_DESCRIPTION));
            int priority = cursor.getInt(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_TASK_PRIORITY));
            String dateStr = cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_TASK_DATE));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_TASK_STATUS));
//            // Parse the date string (example format: "2024-12-07")
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//            String date = null;
//            try {
//                date = formatter.parse(dateStr);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }

            Task task = new Task(id,title, description, priority, dateStr,status);
//            System.out.println(task.getId());
            taskList.add(task);
        }

        cursor.close();
        taskAdapter.notifyDataSetChanged(); // Update RecyclerView
    }
}