package com.developeartexplore.mode_ipc.main.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.developeartexplore.mode_ipc.main.aidl.Book;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.developeartexplore.mode_ipc.main.aidl.IBookManager;
import com.developeartexplore.mode_ipc.main.aidl.IOnNewBookArrvedListener;

/**
 * Created by admin on 2018/3/23.
 */

public class BookManagerService extends Service {

    public static final String TAG = "BookManagerService";

    private AtomicBoolean mIsServiceDestoryed=new AtomicBoolean(false);

    private CopyOnWriteArrayList<Book> mBookList=new CopyOnWriteArrayList<Book>();
    private CopyOnWriteArrayList<IOnNewBookArrvedListener> mOnNewBookArrvedListenerList=
            new CopyOnWriteArrayList<IOnNewBookArrvedListener>();


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
        @Override
        public void registerListener(IOnNewBookArrvedListener listener) throws RemoteException {

            if (!mOnNewBookArrvedListenerList.contains(listener)) {
                Log.i(TAG, "BookManagerService --> registerListener: ");
                mOnNewBookArrvedListenerList.add(listener);
            }else {
                Log.i(TAG, "BookManagerService --> aiready exists: ");
            }
        }

        @Override
        public void unregisterListener(IOnNewBookArrvedListener listener) throws RemoteException {
            Log.i(TAG, "BookManagerService --> unregisterListener: ");
            if (mOnNewBookArrvedListenerList.contains(listener)) {
                Log.i(TAG, "BookManagerService --> unregisterListener: ");
                mOnNewBookArrvedListenerList.remove(listener);
            }else {
                Log.i(TAG, "BookManagerService --> not found ,can not register");
            }
        }

    };


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "BookManagerService --> onCreate: ");
        mBookList.add(new Book(10010,"联通"));
        mBookList.add(new Book(10011,"移动"));
        new Thread(new ServiceWorker()).start();
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
        mIsServiceDestoryed.set(true);
        super.onDestroy();
        Log.i(TAG, "BookManagerService --> onDestroy: ");
    }

    private void onNewBookArrived(Book book) throws RemoteException{
        Log.i(TAG, "BookManagerService -->  onNewBookArrived");
        mBookList.add(book);
        for (int i = 0; i < mOnNewBookArrvedListenerList.size(); i++) {
            IOnNewBookArrvedListener listener=mOnNewBookArrvedListenerList.get(i);
            listener.onNewBookArrived(book);
        }
    }

    private class ServiceWorker implements Runnable{
        @Override
        public void run() {
            Log.i(TAG, "BookManagerService --> ServiceWorker run: ");
            while (!mIsServiceDestoryed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId=mBookList.size()+1;
                Book newBook=new Book(bookId,"new Book#"+bookId);
                try {
                    onNewBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
