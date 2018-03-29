package com.developeartexplore.mode_ipc.main.threads;

/**
 * 建立三个线程，A线程打印10次A，B线程打印10次B,C线程打印10次C，
 * 要求线程同时运行，交替打印10次ABC。
 * 这个问题用Object的 wait()，notify()就可以很方便的解决。代码如下:
 *
 *
 * 程序运行的主要过程就是：
 *     A线程最先运行，持有C,A对象锁，后释放A,C锁，唤醒B。
 *     线程B等待A 锁，再申请B锁，后打印B，再释放B，A锁，唤醒C，
 *     线程C等待B锁，再申请C锁，后打印C，再释放C,B锁，唤醒A。
 *
 */

public class ThreadWait extends Thread {

    private String name;
    private Object prev;
    private Object self;

    public ThreadWait(String name, Object prev, Object self) {
        this.name = name;
        this.prev = prev;
        this.self = self;
    }

    @Override
    public void run() {
        int count = 5;
        while (count > 0) {
            synchronized (prev){
                synchronized (self){
                    System.out.printf(name+" 运行\n");
                    count--;
                    self.notify();
                }
                try {
                    prev.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
