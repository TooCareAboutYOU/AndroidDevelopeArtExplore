package com.developeartexplore.mode_ipc.main.binders;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.CountDownLatch;

/**
 * 1、先绑定远程服务
 * 2、绑定成功后，客户端就通过queryBinder方法去获取各自对应的Binder
 * 3、之后拿到各自所需的Binder，方可进行操作
 */

public class BinderPool {

    public static final int BINDER_NONE = -1;
    public static final int BINDER_COMPUTE = 0;
    public static final int BINDER_SECURITY_CENTER = 1;

    private Context mContext;
    private IBinderPool mIBinderPool;

    private volatile static BinderPool singleton;

    private CountDownLatch mCountDownLatch;

    private BinderPool(Context context) {
        mContext = context.getApplicationContext();
        connectBinderPoolService();
    }

    public static synchronized BinderPool getInstance(Context context) {
        if (singleton == null) {
            synchronized (BinderPool.class) {
                if (singleton == null) {
                    singleton = new BinderPool(context);
                }
            }
        }
        return singleton;
    }

    private synchronized void connectBinderPoolService() {
        mCountDownLatch = new CountDownLatch(1);
        Intent service = new Intent(mContext, BinderPoolService.class);
        mContext.bindService(service, mBinderPoolConnection, Context.BIND_AUTO_CREATE);
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public IBinder queryBinder(int binderCode) {
        IBinder binder = null;
        try {
            if (mIBinderPool != null) {
                binder = mIBinderPool.queryBinder(binderCode);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return binder;
    }

    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIBinderPool = IBinderPool.Stub.asInterface(service);

            try {
                mIBinderPool.asBinder().linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.i(BinderPoolService.TAG, "binderDied: ");
            mIBinderPool.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mIBinderPool = null;
            connectBinderPoolService();
        }
    };


    public static class BinderPoolImpl extends IBinderPool.Stub {

        public BinderPoolImpl() {
            super();
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BINDER_NONE:
                    break;

                case BINDER_COMPUTE:
                    binder = new ComputeImpl();
                    break;

                case BINDER_SECURITY_CENTER:
                    binder = new SecurityCenterImpl();
                    break;
            }

            return binder;
        }
    }

    public static class SecurityCenterImpl extends ISecurityCenter.Stub {

        char SECRET_CODE='^';  // Shift + 6

        @Override
        public String encypt(String content) throws RemoteException {
            char[] chars=content.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                chars[i] ^=SECRET_CODE;
            }
            return new String(chars);
        }

        @Override
        public String decypt(String password) throws RemoteException {
            return encypt(password);
        }
    }


    public static class ComputeImpl extends ICompute.Stub {

        @Override
        public int add(int a, int b) throws RemoteException {
            return a+b;
        }
    }
}
