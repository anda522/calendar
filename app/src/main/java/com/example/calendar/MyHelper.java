package com.example.calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyHelper extends SQLiteOpenHelper {

    public MyHelper(Context context)
    {
        super(context,"sy_10.db3",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE note(date VARCHAR(8) PRIMARY KEY, content VARCHAR(20), type VARCHAR(8))");
//        sqLiteDatabase.execSQL("INSERT INTO note values('2022-12-10','写安卓大作业')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
