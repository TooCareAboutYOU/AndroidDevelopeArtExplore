package com.developeartexplore.mode_ipc.main.threads;

/**
 * Created by admin on 2018/3/29.
 */

public class ThreadYieId extends Thread {

    public ThreadYieId(String name) {
        super(name);
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(getName()+" 运行:"+i);
            //当为3的时候，改线程就会把CPU时间让掉，让其他或者自己的线程执行(也就是谁先抢到谁执行)
            if(i == 3) {
              this.yield();
            }
        }
    }

}
