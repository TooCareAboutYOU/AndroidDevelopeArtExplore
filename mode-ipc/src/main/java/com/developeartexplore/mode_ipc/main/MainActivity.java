 package com.developeartexplore.mode_ipc.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.developeartexplore.mode_ipc.main.aidl.IBookManager;
import com.developeartexplore.mode_ipc.main.service.BookManagerService;

 public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAIDL();
    }

    private boolean isBound;
    private void initAIDL() {
        final ServiceConnection connection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                IBookManager bookManager = IBookManager.Stub.asInterface(iBinder);
                try {
                    Log.i(BookManagerService.TAG, "书本："+bookManager.getBookList().toString());
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
}
