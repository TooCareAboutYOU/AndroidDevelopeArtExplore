package com.developeartexplore.mode_ipc.main.sockets;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Created by admin on 2018/3/26.
 */

public class SocketService extends Service {

    public static final String TAG = "SocketService";

    private boolean isServiceDestroy=false;

    private String[] messages=new String[]{"Hello 你好","What your name?","The weather is so good!","So you next time"};

    ServerSocket serverSocket=null;

    @Override
    public void onCreate() {
        new Thread(new TcPService()).start();
        super.onCreate();
        Log.i(TAG, "Service --> onCreate: ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Service --> onBind: ");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Service -->  onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service --> onDestroy: ");
        isServiceDestroy=true;
//        stopSelf();
        super.onDestroy();
    }

    private class TcPService implements Runnable{

        @Override
        public void run() {
//            ServerSocket serverSocket=null;
            try {
                //监听本地的8688端口
                serverSocket=new ServerSocket(8688);
                // port：指定服务器要绑定的端口（服务器要监听的端口），
                // backlog：指定客户连接请求队列的长度，
                // bindAddr：指定服务器要绑定的IP地址。
                // ServerSocket(int port, int backlog, InetAddress bindAddr) throws IOException

            } catch (IOException e) {
                Log.i(TAG, "Service --> establish tcp service failed,port 8688 ");
                e.printStackTrace();
                return;
            }
            while (!isServiceDestroy) {
                try {
                    Log.i(TAG, "run: "+serverSocket.isClosed());
                    //接收客户端请求

                    final Socket client =serverSocket.accept();
//                    client =serverSocket.accept();
                    Log.i(TAG, "Service --> accept: ");

                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                responseClient(client);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void responseClient(Socket client) throws IOException {
        //用于接收客户端消息
        BufferedReader in=new BufferedReader(new InputStreamReader(client.getInputStream()));

        //用于向客户端发送消息
        PrintWriter out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);

        out.println("欢迎进入聊天室");

//        Log.i(TAG, "Service --> 进入聊天室 ");
        while (!isServiceDestroy) {
            String str=in.readLine();
            Log.i(TAG, "Service --> msg from client: "+str);
//            out.println("Service --> msg from client: "+str);
            if (str == null) {
                Log.i(TAG, "Service --> client is break: ");
                break;
            }
            int i=new Random().nextInt(messages.length);
            String msg=messages[i];
            out.println(msg);   //out : 发送到客户端
        }
        out.close();
        in.close();
        client.close();
    }
}
