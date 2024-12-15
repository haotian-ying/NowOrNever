package com.example.recyclar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class
ToDoDataBase extends SQLiteOpenHelper {

    public static final String CREATE_TODO = "create table ToDo ("
            + "id integer primary key autoincrement, "
            + "matters text, "
            + "time integer)";

    private Context mContext;

    public ToDoDataBase(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO);
        //db.execSQL(CREATE_CATEGORY);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists ToDo");
        onCreate(db);
    }
}
