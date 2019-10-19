package com.atguigu.gmall.item.test;

import java.util.concurrent.CompletableFuture;

public class FutureTest {
   /* public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> {
            System.out.println("初始化创建了一个CompletableFuture对象");
            return "okkkk";
        }).whenComplete((t,u) -> {
            System.out.println("t的值是：以上的结果返回值okkkk"+t);
            System.out.println("u的值是"+u);
        });
    }*/

        public static void main(String[] args) throws InterruptedException {
            /*MyTread myTread1=new MyTread("线程1");
            MyTread myTread2=new MyTread("线程2");
            System.out.println("mainTread");
            myTread1.start();
            myTread2.start();
*/

            MyTread myTread1=new MyTread("线程1");
            MyTread myTread2=new MyTread("线程2");
            myTread1.start();
            for(int i=0;i<20;i++) {
                System.out.println("mainTread"+i);
            }
            myTread2.start();
        }

}
class MyTread extends Thread{
    public MyTread(String name) {
        super(name);
    }
 @Override
    public void run() {
        for (int i = 1; i <= 30; i++) {
            System.out.println(getName()+"：正在执行！"+i);
        }
    }
}

