package com.developeartexplore.mode_ipc.main.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by admin on 2018/3/26.
 */

public class DbOpenHelper extends SQLiteOpenHelper {



    private static final String DB_NAME="book_provider.db";
    public static final String BOOK_TABLE_NAME="book";
    public static final String USER_TABLE_NAME="user";
    private static final int DB_VERSION=1;


    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.i(BookProvider.TAG, "DbOpenHelper: ");
    }


    //图书信息表
    private String CARETE_BOOK_TABLE="CREATE TABLE IF NOT EXISTS "+BOOK_TABLE_NAME+" (_id INTEGER PRIMARY KEY,"+"name TEXT)";
    //用户信息表
    private String CARETE_USER_TABLE="CREATE TABLE IF NOT EXISTS "+USER_TABLE_NAME+" (_id INTEGER PRIMARY KEY,"+"name TEXT,"+"sex INT)";


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(BookProvider.TAG, "onCreate: ");
        db.execSQL(CARETE_BOOK_TABLE);
        db.execSQL(CARETE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(BookProvider.TAG, "onUpgrade: ");
    }
}

