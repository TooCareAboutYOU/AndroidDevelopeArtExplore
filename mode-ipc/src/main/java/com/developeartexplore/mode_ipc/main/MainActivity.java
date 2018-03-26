 package com.developeartexplore.mode_ipc.main;

 import android.annotation.SuppressLint;
 import android.content.ComponentName;
 import android.content.ContentValues;
 import android.content.Context;
 import android.content.Intent;
 import android.content.ServiceConnection;
 import android.database.Cursor;
 import android.net.Uri;
 import android.os.Bundle;
 import android.os.Handler;
 import android.os.IBinder;
 import android.os.Message;
 import android.os.RemoteException;
 import android.os.SystemClock;
 import android.support.v7.app.AppCompatActivity;
 import android.text.TextUtils;
 import android.util.Log;
 import android.view.View;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.TextView;

 import com.developeartexplore.mode_ipc.main.aidl.Book;
 import com.developeartexplore.mode_ipc.main.aidl.IBookManager;
 import com.developeartexplore.mode_ipc.main.aidl.IOnNewBookArrvedListener;
 import com.developeartexplore.mode_ipc.main.binders.BinderPool;
 import com.developeartexplore.mode_ipc.main.binders.BinderPoolService;
 import com.developeartexplore.mode_ipc.main.binders.ICompute;
 import com.developeartexplore.mode_ipc.main.binders.ISecurityCenter;
 import com.developeartexplore.mode_ipc.main.providers.BookProvider;
 import com.developeartexplore.mode_ipc.main.service.BookManagerService;
 import com.developeartexplore.mode_ipc.main.sockets.SocketService;

 import java.io.BufferedReader;
 import java.io.BufferedWriter;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.OutputStreamWriter;
 import java.io.PrintWriter;
 import java.net.Socket;
 import java.text.SimpleDateFormat;
 import java.util.Date;

 public class MainActivity extends AppCompatActivity {


     public static final int MESSAGE_NEW_BOOK_ARRIVED=1;

     private ServiceConnection connection;
     private IBookManager bookManager;

     @SuppressLint("HandlerLeak")
     private Handler mHandler=new Handler(){
         @Override
         public void handleMessage(Message msg) {
             switch (msg.what) {
                 case MESSAGE_NEW_BOOK_ARRIVED:{
                     try {
                         Log.i(BookManagerService.TAG, "Client --> 添加: "+msg.obj+"\n"+
                                 bookManager.getBookList().toString());
                     } catch (RemoteException e) {
                         e.printStackTrace();
                     }
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
        initProvider();
        initSocket();
        initBinderPool();
    }


     /*************************************** BinderPool  ***********************************************/
     private ISecurityCenter mISecurityCenter;
     private ICompute mICompute;
     private void initBinderPool() {
         findViewById(R.id.btn_open_BinderPool).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 new Thread(){
                     @Override
                     public void run() {
                         BinderPool binderPool=BinderPool.getInstance(MainActivity.this);
                         IBinder securityBinder=binderPool.queryBinder(BinderPool.BINDER_SECURITY_CENTER);
                         mISecurityCenter= BinderPool.SecurityCenterImpl.asInterface(securityBinder);
                         Log.i(BinderPoolService.TAG, "Client --> visit: mSecurityCenter" );
                         String msg="helloworld-android";
                         Log.i(BinderPoolService.TAG, "Client --> 待加密数据: "+msg);
                         try {
                             String enMsg=mISecurityCenter.encypt(msg);
                             Log.i(BinderPoolService.TAG, "Client --> 加密enMsg："+enMsg);
                             String deMsg=mISecurityCenter.encypt(enMsg);
                             Log.i(BinderPoolService.TAG, "Client --> 解密deMsg："+deMsg);
                         } catch (RemoteException e) {
                             e.printStackTrace();
                         }

                         IBinder computeBinder=binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
                         mICompute= BinderPool.ComputeImpl.asInterface(computeBinder);
                         try {
                             Log.i(BinderPoolService.TAG, "Client -->  3+5="+mICompute.add(3,5));
                         } catch (RemoteException e) {
                             e.printStackTrace();
                         }
                     }
                 }.start();

             }
         });
     }

     /*************************************** AIDL  ***********************************************/
    private boolean isBound;
    private void initAIDL() {
        connection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                bookManager = IBookManager.Stub.asInterface(iBinder);
                try {
                    Log.i(BookManagerService.TAG, "Client --> 添加前  书本："+bookManager.getBookList().toString());
                    bookManager.addBook(new Book(10086,"Android开发艺术探索"));
                    Log.i(BookManagerService.TAG, "Client --> 添加后  书本："+bookManager.getBookList().toString());

                    bookManager.registerListener(mIOnNewBookArrvedListener);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                isBound=true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                bookManager=null;
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


     /*************************************** Provider  ***********************************************/
     void initProvider(){

         findViewById(R.id.btn_open_Provider).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Log.i(BookProvider.TAG, "触发: ");
//                 Uri uri=Uri.parse("content://com.developeartexplore.mode_ipc.main.providers");
//                 getContentResolver().query(uri,null,null,null,null);
//                 getContentResolver().query(uri,null,null,null,null);
//                 getContentResolver().query(uri,null,null,null,null);

                 Uri bookUri=BookProvider.BOOK_CONTENT_URI;
                 ContentValues contentValues=new ContentValues();
                 contentValues.put("_id",6);;
                 contentValues.put("name","Android开发艺术探索");
                 getContentResolver().insert(bookUri,contentValues);
                 Cursor bookCursor=getContentResolver().query(bookUri,new String[]{"_id","name"},null,null,null);
                 while (bookCursor.moveToNext()) {
                     Book book = new Book();
                     book.setBookId(bookCursor.getInt(0));
                     book.setBookName(bookCursor.getString(1));
                     Log.i(BookProvider.TAG, "Client --> Book 添加后 查询: "+book.toString());
                 }
                 bookCursor.close();


                 Uri userUri=BookProvider.USER_CONTENT_URI;
                 Cursor userCursor=getContentResolver().query(userUri,new String[]{"_id","name","sex"},null,null,null);
                 while (userCursor.moveToNext()) {
                     User user = new User();
                     user.setUserId(userCursor.getInt(0));
                     user.setUserName(userCursor.getString(1));
                     user.setIsMale(userCursor.getInt(2));
                     Log.i(BookProvider.TAG, "Client --> User 添加后 查询: "+user.toString());
                 }
                 userCursor.close();


             }
         });
     }


     /*************************************** Socket  ***********************************************/

     private Intent intentSocket;
     private Socket socketClient;

     private PrintWriter mPrintWriter=null;
     private BufferedReader br=null;

     public static final int MESSAGE_RECEIVE_NEW_MSG=1;
     public static final int MESSAGE_SOCKET_CONNECTED=2;
     private Button btnSend;
     private EditText mEditText;
     private TextView mTextView;


     private Handler handler=new Handler(){
         @Override
         public void handleMessage(Message msg) {
             super.handleMessage(msg);
             switch (msg.what) {
                 case MESSAGE_RECEIVE_NEW_MSG:{
                     Log.i(SocketService.TAG, "Client --> Msg from service: "+msg.obj);
                     mTextView.setText(mTextView.getText().toString()+"\t\t"+msg.obj);
                     break;
                 }
                 case MESSAGE_SOCKET_CONNECTED:{
                     Log.i(SocketService.TAG, "Client --> connect server success: ");
                     btnSend.setEnabled(true);
                     break;
                 }
             }
         }
     };

     private void initSocket() {
         btnSend=findViewById(R.id.btn_send_msg);
         mEditText=findViewById(R.id.et_txt_msg);
         mTextView=findViewById(R.id.tv_getTxt);

         findViewById(R.id.btn_open_Socket).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Log.i(SocketService.TAG, "Client --> 触发");
                 intentSocket=new Intent(MainActivity.this, SocketService.class);
                 startService(intentSocket);
                 new Thread(){
                     @Override
                     public void run() {
                         Log.i(SocketService.TAG, "连接服务器中....: ");
                         connectSocketService();
                     }
                 }.start();
             }
         });


         btnSend.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String sendMsg=mEditText.getText().toString();
                 if (!TextUtils.isEmpty(sendMsg) && mPrintWriter != null) {
                     mPrintWriter.println(sendMsg);
                     mEditText.setText("");
                     String time=FormatDateTime(System.currentTimeMillis());
                     String showMsg= "Client "+time+"： "+sendMsg+"\n";
                     mTextView.setText(mTextView.getText()+"\n"+showMsg);
                 }
             }
         });
     }

     private void connectSocketService() {
         Socket mSocket=null;
         while (mSocket == null) {
             try {
                 mSocket=new Socket("localhost",8688);
                 socketClient=mSocket;
                 mPrintWriter=new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream())),true);
                 handler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
             } catch (IOException e) {
                 btnSend.setEnabled(false);
                 SystemClock.sleep(1000);
                 Log.i(SocketService.TAG, "Client --> connect tcp server failed , auto retrying...: ");
             }
         }


         //接收服务端的消息
         try {
             br=new BufferedReader(new InputStreamReader(mSocket.getInputStream(),"UTF-8"));
             Log.i(SocketService.TAG, "接收服务端的消息: ");
                 while (!MainActivity.this.isFinishing()) {
                     if (br.ready()) {
                         Log.i(SocketService.TAG, "Client --> connectSocketService: ");
                         String msg = br.readLine();
                         if (msg != null) {
                             String time = FormatDateTime(System.currentTimeMillis());
                             String showMsg = "Service " + time + "： " + msg + "\n";
                             handler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG, showMsg).sendToTarget();
                         }

                     }
                 }
                 mPrintWriter.close();
                 br.close();
                 mSocket.close();

         } catch (IOException e) {
             e.printStackTrace();
         }

     }

     private String FormatDateTime(Long time){
         return new SimpleDateFormat("(HH:mm:ss)").format(new Date(time));
     }


     @Override
     protected void onDestroy() {
         if (bookManager != null && bookManager.asBinder().isBinderAlive()) {
             try {
                 bookManager.unregisterListener(mIOnNewBookArrvedListener);
             } catch (RemoteException e) {
                 e.printStackTrace();
             }
         }
         if (isBound) {
             Log.i(BookManagerService.TAG, "Client --> 关闭AIDL通信: ");
             unbindService(connection);
             isBound=false;
         }


         if (socketClient != null) {
             try {
                 socketClient.shutdownInput();
                 socketClient.close();
                 stopService(intentSocket);
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
//         if (intentSocket != null) {
//             stopService(intentSocket);
//         }
         Log.i(SocketService.TAG, "activity onDestroy: ");
         super.onDestroy();
     }

 }
