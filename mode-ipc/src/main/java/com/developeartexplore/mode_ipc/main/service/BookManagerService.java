package com.developeartexplore.mode_ipc.main.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.developeartexplore.mode_ipc.main.aidl.Book;
import com.developeartexplore.mode_ipc.main.aidl.IBookManager;
import com.developeartexplore.mode_ipc.main.aidl.IOnNewBookArrvedListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *  IPS---->  AIDL
 */

public class BookManagerService extends Service {

    public static final String TAG = "BookManagerService";

    private AtomicBoolean mIsServiceDestoryed=new AtomicBoolean(false);

    private CopyOnWriteArrayList<Book> mBookList=new CopyOnWriteArrayList<Book>();
//    private CopyOnWriteArrayList<IOnNewBookArrvedListener> mOnNewBookArrvedListenerList=
//            new CopyOnWriteArrayList<IOnNewBookArrvedListener>();

    private RemoteCallbackList<IOnNewBookArrvedListener> mOnNewBookArrvedListenerList=
            new RemoteCallbackList<IOnNewBookArrvedListener>();

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
//            if (!mOnNewBookArrvedListenerList.contains(listener)) {
//                Log.i(TAG, "BookManagerService --> registerListener: ");
//                mOnNewBookArrvedListenerList.add(listener);
//            }else {
//                Log.i(TAG, "BookManagerService --> aiready exists: ");
//            }
            mOnNewBookArrvedListenerList.register(listener);
//            Log.i(TAG, "BookManagerService -->  registerListener listener数量: "+mOnNewBookArrvedListenerList.beginBroadcast());

        }

        @Override
        public void unregisterListener(IOnNewBookArrvedListener listener) throws RemoteException {
            Log.i(TAG, "BookManagerService --> unregisterListener: ");
//            if (mOnNewBookArrvedListenerList.contains(listener)) {
//                Log.i(TAG, "BookManagerService --> unregisterListener: ");
//                mOnNewBookArrvedListenerList.remove(listener);
//            }else {
//                Log.i(TAG, "BookManagerService --> not found ,can not register");
//            }

            mOnNewBookArrvedListenerList.unregister(listener);
//            Log.i(TAG, "BookManagerService -->  unregisterListener listener数量: "+mOnNewBookArrvedListenerList.beginBroadcast());
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


    private MyBinder sBinder=new MyBinder();  //true 就返回sBinder
    //权限验证方式二
    private class MyBinder extends Binder {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int check=checkCallingOrSelfPermission("com.developeartexplore.mode_ipc.main.permission.ACCESS_BOOK_SERVICE");
            if (check == PackageManager.PERMISSION_DENIED) {
                Log.i(TAG, "BookManagerService --> onBind: false");
                return false;
            }
            String packageName=null;
            String[] packates=getPackageManager().getPackagesForUid(getCallingUid());
            if (packates != null && packates.length > 0) {
                packageName=packates[0];
            }
            if (!packageName.startsWith("com.developeartexplore")) {
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "BookManagerService --> onBind: ");
        //权限验证方式一
        int check=checkCallingOrSelfPermission("com.developeartexplore.mode_ipc.main.permission.ACCESS_BOOK_SERVICE");
        if (check == PackageManager.PERMISSION_DENIED) {
            Log.i(TAG, "BookManagerService --> onBind: null");
            return null;
        }
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
//        for (int i = 0; i < mOnNewBookArrvedListenerList.size(); i++) {
//            listener=mOnNewBookArrvedListenerList.get(i);
//            listener.onNewBookArrived(book);
//        }

        int size=mOnNewBookArrvedListenerList.beginBroadcast();

        for (int i = 0; i < size; i++) {
            IOnNewBookArrvedListener listener=mOnNewBookArrvedListenerList.getBroadcastItem(i);
            if (listener != null) {
                listener.onNewBookArrived(book);
            }
//            Log.i(TAG, "BookManagerService -->  listener数量: "+mOnNewBookArrvedListenerList.beginBroadcast());
        }
        mOnNewBookArrvedListenerList.finishBroadcast();
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
