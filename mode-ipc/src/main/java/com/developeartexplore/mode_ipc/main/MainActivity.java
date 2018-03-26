 package com.developeartexplore.mode_ipc.main;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.developeartexplore.mode_ipc.main.aidl.Book;
import com.developeartexplore.mode_ipc.main.aidl.IBookManager;
import com.developeartexplore.mode_ipc.main.aidl.IOnNewBookArrvedListener;
import com.developeartexplore.mode_ipc.main.service.BookManagerService;

 public class MainActivity extends AppCompatActivity {


     public static final int MESSAGE_NEW_BOOK_ARRIVED=1;

     private ServiceConnection connection;

     private IBookManager mRemoteBookManager;

     @SuppressLint("HandlerLeak")
     private Handler mHandler=new Handler(){
         @Override
         public void handleMessage(Message msg) {
             switch (msg.what) {
                 case MESSAGE_NEW_BOOK_ARRIVED:{
                     Log.i(BookManagerService.TAG, "Client --> handleMessage: "+msg.obj);
                     break;
                 }
                 default:
                     super.handleMessage(msg);

             }
         }
     };


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAIDL();
    }

    private boolean isBound;
    private void initAIDL() {
        connection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                IBookManager bookManager = IBookManager.Stub.asInterface(iBinder);
                try {
                    Log.i(BookManagerService.TAG, "Client --> 添加前  书本："+bookManager.getBookList().toString());
                    bookManager.addBook(new Book(10086,"Android开发艺术探索"));
                    Log.i(BookManagerService.TAG, "Client --> 添加后  书本："+bookManager.getBookList().toString());


                    mRemoteBookManager=bookManager;
                    bookManager.registerListener(mIOnNewBookArrvedListener);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                isBound=true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                isBound=false;
            }
        };

        findViewById(R.id.btn_open_aidl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(BookManagerService.TAG, "Client --> 开启AIDL通信: ");
                Intent intent=new Intent(MainActivity.this,BookManagerService.class);
                bindService(intent,connection, Context.BIND_AUTO_CREATE);
            }
        });

        findViewById(R.id.btn_close_aidl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBound) {
                    Log.i(BookManagerService.TAG, "Client --> 关闭AIDL通信: ");
                    unbindService(connection);
                    isBound=false;
                }
            }
        });
    }

    private IOnNewBookArrvedListener mIOnNewBookArrvedListener=new IOnNewBookArrvedListener.Stub() {
        @Override
        public void onNewBookArrived(Book newbook) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED,newbook).sendToTarget();
        }
    };

     @Override
     protected void onDestroy() {
         if (mRemoteBookManager != null && mRemoteBookManager.asBinder().isBinderAlive()) {
             try {
                 mRemoteBookManager.unregisterListener(mIOnNewBookArrvedListener);
             } catch (RemoteException e) {
                 e.printStackTrace();
             }
         }
         if (isBound) {
             Log.i(BookManagerService.TAG, "Client --> 关闭AIDL通信: ");
             unbindService(connection);
             isBound=false;
         }
         super.onDestroy();
     }
 }
