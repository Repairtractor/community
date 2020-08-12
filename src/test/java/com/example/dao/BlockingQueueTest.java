package com.example.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.IntStream;

/**
 * 利用blockingqueue测试生产者和消费者模式
 */
@SpringBootTest
public class BlockingQueueTest {

    @Test
    public void test() {

    }

    public static void main(String[] args) {
        BlockingQueue<Integer> queue=new ArrayBlockingQueue<Integer>(10);
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }
}

class Producer implements Runnable {

    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        //stream流循环
        IntStream.range(0, 100).forEach(e -> {
            try {
                Thread.sleep(60);
                queue.put(e);
                System.out.println(Thread.currentThread().getName()+"生产："+queue.size());
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        });
    }
}

class Consumer implements Runnable {
    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        IntStream.range(0,Integer.MAX_VALUE).forEach((e)->{
            try {
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName()+"消费："+queue.size());
            }catch (InterruptedException e1){
                e1.printStackTrace();
            }
        });
    }
}














