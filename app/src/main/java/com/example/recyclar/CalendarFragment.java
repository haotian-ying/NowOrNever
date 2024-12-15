package com.example.recyclar;

import static com.example.recyclar.TaskDatabaseHelper.TABLE_TODOS;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment implements ToDoAdapter.OnToDoItemChangeListener{
    private ImageView ivToggle;
    private CalendarView calendarView;
    private boolean isExpanded = false;
    private CalendarLayout calendarLayout;
    private RecyclerView rvToDo;
    private RecyclerView rvDone;
    private FloatingActionButton fab;
    private ToDoAdapter todoAdapter;
    private ToDoAdapter doneAdapter;
    // 存储所有基类ToDo
    private List<ToDo> baseToDoList = new ArrayList<ToDo>();
    private List<EveryToDo> todoList = new ArrayList<EveryToDo>();
    private List<EveryToDo> doneList = new ArrayList<EveryToDo>();
    private List<List<EveryToDo>> ToDoMap;
    private SharedViewModel viewModel;
    private TaskDatabaseHelper dbHelper;
    private String currentDate;
    private String today;
    public static CalendarFragment newInstance(String param1, String param2) {
        return new CalendarFragment();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        view.setBackgroundColor(Color.parseColor("#F5F5F5"));

        calendarView = view.findViewById(R.id.calendarView);
        ivToggle = view.findViewById(R.id.ivToggle);
        calendarLayout = view.findViewById(R.id.calendarLayout);
        rvToDo = view.findViewById(R.id.rvToDo);
        rvDone = view.findViewById(R.id.rvDone);
        fab = view.findViewById(R.id.fab);
        // 初始化数据库
        if (getContext() == null) {
            System.out.println("Context is null");
        } else {
            System.out.println("Context is valid");
        }
        dbHelper = new TaskDatabaseHelper(getContext());

        // 初始化 ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel.getNewTodo().observe(getViewLifecycleOwner(), new Observer<ToDo>() {
            @Override
            public void onChanged(ToDo todo) {
                // ToDo写入数据库
                addToDo(todo);
                baseToDoList.add(todo);
                Toast.makeText(getContext(),todo.getTitle(), Toast.LENGTH_SHORT).show();

                Date date = new Date();
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
                today = sDateFormat.format(date);
                EveryToDo todayToDo = new EveryToDo(todo,today,false);
                // Toast.makeText(getContext(),todayToDo.getDate(), Toast.LENGTH_SHORT).show();
                // 今日EveryToDo写入数据库
                System.out.println("todo_id:"+todo.getId());
                System.out.println("every_todo_id:"+todayToDo.getId());
                System.out.println("every_id:"+todayToDo.getEveryId());
                addEveryToDo(todayToDo);
                todoList.add(todayToDo);
                todoAdapter.notifyDataSetChanged();
            }
        });

        initTodo();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvToDo.setLayoutManager(layoutManager);
        todoAdapter = new ToDoAdapter(todoList);
        todoAdapter.setOnToDoItemChangeListener(this);
        rvToDo.setAdapter(todoAdapter);

        LinearLayoutManager DonelayoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(DonelayoutManager.VERTICAL);
        rvDone.setLayoutManager(DonelayoutManager);
        doneAdapter = new ToDoAdapter(doneList);
        doneAdapter.setOnToDoItemChangeListener(this);
        rvDone.setAdapter(doneAdapter);

        // 默认从当前日期开始
        Date date = new Date();
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
        currentDate = sDateFormat.format(date);
        today = sDateFormat.format(date);
        // 找到今日todo
        updateRecyclerViewForDate(today);

        calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {

            }

            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                // 处理日期选择事件
                String selectedDate = calendar.getYear() + "-"
                        + String.format("%02d", calendar.getMonth()) + "-"
                        + String.format("%02d", calendar.getDay());
                // 两次选中日期不同才进行更新
                if(!Objects.equals(currentDate, selectedDate)){
                    //Toast.makeText(getContext(), "Selected Date: " + currentDate, Toast.LENGTH_SHORT).show();
                    // TODO list 的更新
                    // 今天之前的日期
                    int compareResult = selectedDate.compareTo(today);
                    if(compareResult < 0){
                        // 读取EveryToDo
                        updateRecyclerViewForDate(selectedDate);
                    }
                    else if(compareResult == 0){
                        // 先读取EveryToDo，保存所有ToDo id
                        updateRecyclerViewForDate(selectedDate);
                        Toast.makeText(getContext(), "选择今天", Toast.LENGTH_SHORT).show();
                        // 根据ToDo id ，再读ToDo找遗漏

                    }
                    else{
                        // 仅读取ToDo
                        // 不写回数据库
                        updateRecyclerViewForFuture(selectedDate);
                    }
                    currentDate = selectedDate;
                }

                System.out.println(calendar.toString());
            }
        });
        // 设置视图切换监听器
        calendarView.setOnViewChangeListener(new CalendarView.OnViewChangeListener() {
            @Override
            public void onViewChange(boolean isMonthView) {
                Toast.makeText(getContext(), isMonthView ? "Month View" : "Week View", Toast.LENGTH_SHORT).show();
                if (isMonthView) {
                    calendarLayout.expand(240); // 展开日历
                } else {
                    calendarLayout.shrink(); // 收起日历
                }
                isExpanded = !isExpanded;
            }
        });

        // ivToggle 点击事件：切换日历视图
        /*
        ivToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 切换视图：如果当前是月视图，则切换到周视图，反之亦然
                if(calendarLayout.getVisibility() != VISIBLE)
                    Toast.makeText(getContext(),"expand", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(),"shrink", Toast.LENGTH_SHORT).show();
            }

        });
        */

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddToDoFragment bottomSheetFragment = new AddToDoFragment();
                bottomSheetFragment.show(getParentFragmentManager(), "AddToDoFragment");
            }
        });

        return view;
    }


    private void initTodo(){
        Date d = new Date();
        boolean repeat = false;
        ToDo sample = new ToDo("早起", 9, 0,repeat,true);
        ToDo done = new ToDo("早睡", 9, 0,repeat,true);
        baseToDoList.add(sample);
        baseToDoList.add(done);
        EveryToDo sampleToday1 = new EveryToDo(sample,d.toString(),false);
        EveryToDo sampleToday2 = new EveryToDo(sample,d.toString(),false);
        EveryToDo sampleToday3 = new EveryToDo(sample,d.toString(),false);
        EveryToDo sampleToday4 = new EveryToDo(sample,d.toString(),false);
        EveryToDo sampleDone = new EveryToDo(done,d.toString(),true);
        todoList.add(sampleToday1);
        todoList.add(sampleToday2);
        todoList.add(sampleToday3);
        todoList.add(sampleToday4);

        doneList.add(sampleDone);
    }

    @Override
    public void onStatusChanged(EveryToDo item, boolean isCompleted) {
        Toast.makeText(getContext(), "recyclerView 变化：", Toast.LENGTH_SHORT).show();
        // 状态切换时的回调
        if (isCompleted) {
            todoList.remove(item);
            doneList.add(item);
            Toast.makeText(getContext(), "任务完成：" + item.getTitle(), Toast.LENGTH_SHORT).show();
        } else {
            doneList.remove(item);
            todoList.add(item);
            Toast.makeText(getContext(), "任务未完成：" + item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        updateTaskStatusInDatabase(item,isCompleted);

        todoAdapter.notifyDataSetChanged();
        doneAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemDeleted(EveryToDo item) {
        // 删除任务的回调
        if (item.isDone()) {
            doneList.remove(item);
        } else {
            todoList.remove(item);
        }

        todoAdapter.notifyDataSetChanged();
        doneAdapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "任务已删除：" + item.getTitle(), Toast.LENGTH_SHORT).show();
    }
//
//    public static int[] stringToInts(String s){
//        int[] n = new int[7];
//        int cnt = 0;
//        for(int i = 0;i<s.length();i++){
//            if(n[i] == ',')
//                continue;
//            else {
//                char c = s.charAt(i);
//                n[cnt++] = c - '0';
//            }
//        }
//        return n;
//    }

    private void updateRecyclerViewForDate(String date) {
        TaskDatabaseHelper dbHelper = new TaskDatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        System.out.println("sql"+date);
        // 查询 `every_todos` 和 `todos` 联合数据
        String query = "SELECT et.every_id AS every_id, et.todo_id, et.date, et.is_done, " +
                "t.id AS todo_id, t.title, t.hour_due, t.minute_due, t.repeat, t.set_notification " +
                "FROM every_todos et " +
                "JOIN todos t ON et.todo_id = t.id " +
                "WHERE et.date = ?";
        Cursor cursor = db.rawQuery(query, new String[]{date});

        todoList.clear();
        doneList.clear();

        while (cursor.moveToNext()) {
            //Toast.makeText(getContext(), "存在EveryToDo", Toast.LENGTH_SHORT).show();
            // 获取 `ToDo` 数据
            String todoId = cursor.getString(cursor.getColumnIndexOrThrow("todo_id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            int hourDue = cursor.getInt(cursor.getColumnIndexOrThrow("hour_due"));
            int minuteDue = cursor.getInt(cursor.getColumnIndexOrThrow("minute_due"));
            boolean repeat = cursor.getInt(cursor.getColumnIndexOrThrow("repeat")) == 1;
            boolean notification = cursor.getInt(cursor.getColumnIndexOrThrow("set_notification")) == 1;

            // 创建 `ToDo` 对象
            ToDo todo = new ToDo(todoId, title, hourDue, minuteDue, repeat, notification);

            // 获取 `EveryToDo` 数据
            String everyId = cursor.getString(cursor.getColumnIndexOrThrow("every_id"));
            String taskDate = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            boolean isDone = cursor.getInt(cursor.getColumnIndexOrThrow("is_done")) == 1;

            // 创建 `EveryToDo` 对象
            EveryToDo everyToDo = new EveryToDo(todo,everyId,taskDate, isDone);
            if (isDone) {
                doneList.add(everyToDo);
            } else {
                // Toast.makeText(getContext(), "存在EveryToDo"+everyToDo.getTitle(), Toast.LENGTH_SHORT).show();
                todoList.add(everyToDo);
            }
        }
        cursor.close();

        // 更新 RecyclerView
        todoAdapter.notifyDataSetChanged();
        doneAdapter.notifyDataSetChanged();

        db.close();
    }

    private void updateRecyclerViewForFuture(String date) {
        TaskDatabaseHelper dbHelper = new TaskDatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 查询 `every_todos` 和 `todos` 联合数据
        String query = "SELECT t.id AS todo_id, t.title, t.hour_due, t.minute_due, t.repeat, t.set_notification " +
                "FROM todos t " +
                "WHERE t.repeat = 1";
        Cursor cursor = db.rawQuery(query,null);

        todoList.clear();
        doneList.clear();

        while (cursor.moveToNext()) {
            //Toast.makeText(getContext(), "存在EveryToDo", Toast.LENGTH_SHORT).show();
            // 获取 `ToDo` 数据
            String todoId = cursor.getString(cursor.getColumnIndexOrThrow("todo_id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            int hourDue = cursor.getInt(cursor.getColumnIndexOrThrow("hour_due"));
            int minuteDue = cursor.getInt(cursor.getColumnIndexOrThrow("minute_due"));
            boolean repeat = cursor.getInt(cursor.getColumnIndexOrThrow("repeat")) == 1;
            boolean notification = cursor.getInt(cursor.getColumnIndexOrThrow("set_notification")) == 1;

            // 创建 `ToDo` 对象
            ToDo todo = new ToDo(todoId, title, hourDue, minuteDue, repeat, notification);

            // 创建 `EveryToDo` 对象
            EveryToDo everyToDo = new EveryToDo(todo,date, false);
            // Toast.makeText(getContext(), "存在EveryToDo"+everyToDo.getTitle(), Toast.LENGTH_SHORT).show();
            todoList.add(everyToDo);
        }
        cursor.close();

        // 更新 RecyclerView
        todoAdapter.notifyDataSetChanged();
        doneAdapter.notifyDataSetChanged();

        db.close();
    }

    public void addToDo(ToDo todo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TaskDatabaseHelper.COLUMN_TODO_ID, todo.getId());
        values.put(TaskDatabaseHelper.COLUMN_TODO_TITLE, todo.getTitle());
        values.put(TaskDatabaseHelper.COLUMN_TODO_HOUR_DUE, todo.getHourDue());
        values.put(TaskDatabaseHelper.COLUMN_TODO_MINUTE_DUE, todo.getMinuteDue());
        values.put(TaskDatabaseHelper.COLUMN_TODO_REPEAT,todo.getRepeat()? 1 : 0);
        values.put(TaskDatabaseHelper.COLUMN_TODO_NOTIFICATION, todo.hasNotification() ? 1 : 0);

        long result = db.insert(TABLE_TODOS, null, values);
        db.close();
    }

    public void addEveryToDo(EveryToDo everyToDo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        System.out.println("add"+everyToDo.getDate());
        // 插入 EveryToDo 的数据
        values.put("every_id", everyToDo.getEveryId());
        values.put("todo_id", everyToDo.getId());
        values.put("date", everyToDo.getDate());
        values.put("is_done", everyToDo.isDone() ? 1 : 0);


        try {
            long result = db.insert("every_todos", null, values);
            if (result == -1) {
                System.out.println("插入失败");
                Toast.makeText(getContext(), "插入 EveryToDo 失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "成功添加 EveryToDo", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "数据库错误: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    private void updateTaskStatusInDatabase(EveryToDo item, boolean isCompleted) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 更新任务状态
        ContentValues values = new ContentValues();
        values.put("is_done", isCompleted ? 1 : 0); // 假设数据库中的状态字段是 isCompleted，1 表示完成，0 表示未完成

        String whereClause = "every_id = ?";
        String[] whereArgs = new String[]{String.valueOf(item.getEveryId())}; // 根据任务ID更新状态

        db.update("every_todos", values, whereClause, whereArgs); // "tasks" 是数据库中任务表的表名
        db.close();
    }
}