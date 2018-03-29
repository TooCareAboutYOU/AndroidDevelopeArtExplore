package com.developeartexplore.mode_ipc.main.threads;

/**
 * Created by admin on 2018/3/29.
 */

public class ExeApplications {

    public static void main(String[] args) {

//        initThread();
//        initRunnable();
//        initJoinThread();
//        initThreadYieId();

        initThreadWait();
    }

    private static void initThread() {
        MyThread myThread1=new MyThread("A");
        MyThread myThread2=new MyThread("B");
        myThread1.start();
        myThread2.start();
    }


    private static void initRunnable() {

//        MyRunnable myRunnable1=new MyRunnable("A");
//        MyRunnable myRunnable2=new MyRunnable("B");
//        myRunnable1.run();
//        myRunnable2.run();

        new Thread(new MyRunnable("A")).start();
        new Thread(new MyRunnable("B")).start();
    }


    /**
     * 在很多情况下，主线程生成并起动了子线程，如果子线程里要进行大量的耗时的运算，主线程往往将于子线程之前结束，
     * 但是如果主线程 处理完其他的事务后， 需要用到子线程的处理结果，也就是主线程需要等待子线程执行完成之后再结束，
     * 这个时候就要用到join()方法了。
     */
    private static void initJoinThread() {
        System.out.printf("主线程运行开始\n");
        try {
            MyThread myThread1=new MyThread("A");
            MyThread myThread2=new MyThread("B");
            myThread1.start();
            myThread2.start();
            myThread1.join();
            myThread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("主线程运行结束");
    }


    private static void initThreadYieId() {
        ThreadYieId threadYieId1=new ThreadYieId("A");
        ThreadYieId threadYieId2=new ThreadYieId("B");
        threadYieId1.start();
        threadYieId2.start();
    }

    static Object a=new Object();
    static Object b=new Object();
    static Object c=new Object();
    private static void initThreadWait() {
        ThreadWait threadWait1=new ThreadWait("A",c,a);
        ThreadWait threadWait2=new ThreadWait("B",a,b);
        ThreadWait threadWait3=new ThreadWait("C",b,c);
        try {
            new Thread(threadWait1).start();
            Thread.sleep(100);  //确保按顺序A、B、C执行
            new Thread(threadWait2).start();
            Thread.sleep(100);
            new Thread(threadWait3).start();
            Thread.sleep(100);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

