package com.example.tryking.practice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Tryking on 2017/2/6.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_TABLE_VIDEO = "create table Video ("
            + "id integer primary key autoincrement, "
            + "name text, "
            + "time text, "
            + "size integer)";
    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                            int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_VIDEO);
        Toast.makeText(mContext, "创建表Video成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Toast.makeText(mContext, "开始更新数据库，旧版本：" + oldVersion + "；新版本：" + newVersion, Toast
                .LENGTH_SHORT).show();
    }
}
