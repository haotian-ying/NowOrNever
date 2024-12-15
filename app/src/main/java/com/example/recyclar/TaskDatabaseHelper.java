package com.example.recyclar;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Arrays;

public class TaskDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "taskManager.db";
    private static final int DATABASE_VERSION = 1;

    // 表名
    public static final String TABLE_TASKS = "tasks";
    public static final String TABLE_TODOS = "todos";
    public static final String TABLE_POMODORO = "pomodoros";

    // 列名（任务表）
    public static final String COLUMN_TASK_ID = "_id";
    public static final String COLUMN_TASK_TITLE = "title";
    public static final String COLUMN_TASK_PRIORITY = "priority";
    public static final String COLUMN_TASK_DESCRIPTION = "description";
    public static final String COLUMN_TASK_STATUS = "status";
    public static final String COLUMN_TASK_DATE = "date";

    // 列名（待办事项表）
    //ToDo 表
    public static final String COLUMN_TODO_ID = "id";
    public static final String COLUMN_TODO_TITLE = "title";
    public static final String COLUMN_TODO_HOUR_DUE = "hour_due";
    public static final String COLUMN_TODO_MINUTE_DUE = "minute_due";
    public static final String COLUMN_TODO_REPEAT = "repeat";
    public static final String COLUMN_TODO_NOTIFICATION = "set_notification";

    // EveryToDo 表
    public static final String TABLE_EVERY_TODOS = "every_todos";
    public static final String COLUMN_EVERY_TODO_ID = "every_id";
    public static final String COLUMN_EVERY_TODO_TODO_ID = "todo_id";
    public static final String COLUMN_EVERY_TODO_DATE = "date";
    public static final String COLUMN_EVERY_TODO_IS_DONE = "is_done";

    // 列名（番茄钟表）
    public static final String COLUMN_POMODORO_ID = "_id";
    public static final String COLUMN_POMODORO_DURATION = "duration"; // 持续时间（分钟）
    public static final String COLUMN_POMODORO_TIMESTAMP = "timestamp"; // 时间戳

    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 创建任务表
    String CREATE_TASK_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
            + COLUMN_TASK_ID + " TEXT,"
            + COLUMN_TASK_TITLE + " TEXT,"
            + COLUMN_TASK_DESCRIPTION + " TEXT,"
            + COLUMN_TASK_PRIORITY + " INTEGER,"
            + COLUMN_TASK_DATE + " TEXT,"
            + COLUMN_TASK_STATUS + " INTEGER"
            + ")";

    // 创建待办事项表
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODOS + "("
            + COLUMN_TODO_ID + " TEXT PRIMARY KEY, "
            + COLUMN_TODO_TITLE + " TEXT, "
            + COLUMN_TODO_HOUR_DUE + " INTEGER, "
            + COLUMN_TODO_MINUTE_DUE + " INTEGER, "
            + COLUMN_TODO_REPEAT + " INTEGER, "
            + COLUMN_TODO_NOTIFICATION + " INTEGER)";

    // 创建每日todo
    private static final String CREATE_EVERY_TODO_TABLE = "CREATE TABLE " + TABLE_EVERY_TODOS + "("
            + COLUMN_EVERY_TODO_ID + " TEXT PRIMARY KEY, "
            + COLUMN_EVERY_TODO_TODO_ID + " TEXT, "
            + COLUMN_EVERY_TODO_DATE + " TEXT, "
            + COLUMN_EVERY_TODO_IS_DONE + " INTEGER, "
            + "FOREIGN KEY(" + COLUMN_EVERY_TODO_TODO_ID + ") REFERENCES " + TABLE_TODOS + "(" + COLUMN_TODO_ID + "))";

    // 创建番茄钟表
    String CREATE_POMODORO_TABLE = "CREATE TABLE " + TABLE_POMODORO + "("
            + COLUMN_POMODORO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_POMODORO_DURATION + " INTEGER,"
            + COLUMN_POMODORO_TIMESTAMP + " INTEGER"
            + ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TASK_TABLE);
        db.execSQL(CREATE_TODO_TABLE);
        db.execSQL(CREATE_EVERY_TODO_TABLE);
        db.execSQL(CREATE_POMODORO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // 删除旧的表
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVERY_TODOS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_POMODORO);
            // 创建新的表
            db.execSQL(CREATE_TASK_TABLE);
            db.execSQL(CREATE_TODO_TABLE);
            db.execSQL(CREATE_EVERY_TODO_TABLE);
            db.execSQL(CREATE_POMODORO_TABLE);
        }
    }
}
