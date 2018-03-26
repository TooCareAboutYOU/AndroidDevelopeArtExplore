package com.developeartexplore.mode_ipc.main.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 *  * insert delete update query存在多线程并发访问，
 * 因此方法内部需要做好线程同步访问
 */

public class BookProvider extends ContentProvider {

    public static final String TAG = "BookProvider";
    public static final String AUTHORITY="com.developeartexplore.mode_ipc.main.providers";
    public static final Uri BOOK_CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/book");
    public static final Uri USER_CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/user");
    public static final int BOOK_URI_CODE=0;
    public static final int USER_URI_CODE=1;
    private static UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY,"book",BOOK_URI_CODE);
        sUriMatcher.addURI(AUTHORITY,"user",USER_URI_CODE);
    }

    private Context mContext;
    private SQLiteDatabase mDb;

    private String getTableName(Uri uri){
        String tableName=null;
        switch (sUriMatcher.match(uri)) {
            case BOOK_URI_CODE:{
                tableName=DbOpenHelper.BOOK_TABLE_NAME;
                break;
            }
            case USER_URI_CODE:{
                tableName=DbOpenHelper.USER_TABLE_NAME;
                break;
            }
            default:break;
        }
        return tableName;
    }

    @Override
    public boolean onCreate() {
        Log.i(TAG, "BookProvider --> onCreate thread: "+Thread.currentThread().getName());
        mContext=getContext();
        //ContentProvider创建时，初始化数据库。注意：这里仅仅为了演示，实际情况下不推荐在主线程中进行耗时的数据库操作
        iniProviderData();
        return true;
    }

    private void iniProviderData() {
        mDb=new DbOpenHelper(mContext).getWritableDatabase();
        mDb.execSQL("delete from "+DbOpenHelper.BOOK_TABLE_NAME);
        mDb.execSQL("delete from "+DbOpenHelper.USER_TABLE_NAME);

        mDb.execSQL("insert into book values(3,'Android');");
        mDb.execSQL("insert into book values(4,'Ios');");
        mDb.execSQL("insert into book values(5,'Html5');");
        mDb.execSQL("insert into user values(1,'安卓',1);");
        mDb.execSQL("insert into user values(2,'苹果',0);");
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.i(TAG, "BookProvider --> query thread: "+Thread.currentThread().getName());
        String table=getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("Unsupport Uri："+uri);
        }
        return mDb.query(table,projection,selection,selectionArgs,null,null,sortOrder,null);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Log.i(TAG, "BookProvider --> getType: "+Thread.currentThread().getName());
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.i(TAG, "BookProvider --> insert thread: "+Thread.currentThread().getName());
        String table=getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("Unsupport Uri："+uri);
        }
        mDb.insert(table,null,values);
        mContext.getContentResolver().notifyChange(uri,null);

        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.i(TAG, "BookProvider --> delete thread: "+Thread.currentThread().getName());
        String table=getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("Unsupport Uri："+uri);
        }
        int count = mDb.delete(table,selection,selectionArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.i(TAG, "BookProvider --> update thread: "+Thread.currentThread().getName());

        String table=getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("Unsupport Uri："+uri);
        }
        int row=mDb.update(table,values,selection,selectionArgs);
        if (row > 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return row;
    }


}
