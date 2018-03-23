package com.developeartexplore.mode_ipc.main.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.developeartexplore.mode_ipc.main.aidl.Book;
import com.developeartexplore.mode_ipc.main.aidl.IBookManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 2018/3/23.
 */

public class BookManagerService extends Service {

    public static final String TAG = "BookManagerService";


    private CopyOnWriteArrayList<Book> mBookList=new CopyOnWriteArrayList<Book>();

    private Binder mBinder=new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            Log.i(TAG, "BookManagerService --> getBookList: ");
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            Log.i(TAG, "BookManagerService --> addBook: ");
            mBookList.add(book);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "BookManagerService --> onCreate: ");
        mBookList.add(new Book(10010,"联通"));
        mBookList.add(new Book(10011,"移动"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "BookManagerService --> onBind: ");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "BookManagerService --> onUnbind: ");
        return super.onUnbind(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "BookManagerService --> onDestroy: ");
    }
}
