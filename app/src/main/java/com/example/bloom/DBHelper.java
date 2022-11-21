package com.example.bloom;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    final static String DATABASE_NAME = "db_tum_obat";
    final static String TBL_TIMER = "tbl_Timer";
    final static String TBL_TASK = "tbl_Task";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    public void onCreate(SQLiteDatabase db) {
        String Timersql = "CREATE TABLE IF NOT EXISTS " + TBL_TIMER + " (_id TEXT PRIMARY KEY, nama TEXT, batas INTEGER, durasi INTEGER)";
        String Tasksql = "CREATE TABLE IF NOT EXISTS " + TBL_TASK + " (_id TEXT PRIMARY KEY, isDone INTEGER, nama TEXT, tag TEXT, deskripsi TEXT, tanggal TEXT, jam TEXT)";
        db.execSQL(Timersql);
        db.execSQL(Tasksql);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_TIMER);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_TASK);

        onCreate(db);
    }
}
