package kr.heeseong.chatting.user.model;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;

class ChattingUserDataTest {


    @Test
    void QueueTest() throws InterruptedException {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

        queue.put("추가1");
        queue.put("추가2");
        queue.put("추가3");
        queue.put("추가4");
        queue.put("추가5");
        queue.put("추가6");
        queue.put("추가7");
        queue.put("추가8");
        queue.put("추가9");
        queue.put("추가10");
        queue.take();
        queue.take();
        queue.take();
        queue.take();
        queue.take();
        queue.take();
        queue.take();
        queue.take();
        queue.take();
        System.out.println(queue);
        queue.take();
        System.out.println(queue);
        queue.take();
    }

    @Test
    void finalTest() {
        final Long test;
    }

}