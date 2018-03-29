package com.developeartexplore.mode_ipc.main.threads;

/**
 * Created by admin on 2018/3/29.
 */

public class MyRunnable implements Runnable {

    private String name;

    public MyRunnable(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.printf(this.name+" 运行："+i+"\n");
            try {
                Thread.sleep((int)Math.random()*10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
