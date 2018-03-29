package com.developeartexplore.mode_ipc.main.threads;

/**
 *
 */

public class MyThread extends Thread {

    private String name;

    public MyThread(String name) {
        this.name = name;
    }

    public void run(){
        for (int i = 0; i < 5; i++) {
            System.out.printf(this.name+" 运行："+i+"\n");
            try {
                sleep((int)Math.random()*10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}

