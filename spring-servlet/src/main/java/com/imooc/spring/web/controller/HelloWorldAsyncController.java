package com.imooc.spring.web.controller;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 * Hello World 异步 {@link RestController}
 */
@RestController
@EnableScheduling
public class HelloWorldAsyncController {


    private final BlockingQueue<DeferredResult<String>> queue = new ArrayBlockingQueue<>(5);

    // 超时随机数
    private final Random random = new Random();


    @Scheduled(fixedRate = 5000)
    private void process() throws InterruptedException { // 定时操作
        DeferredResult<String> result = null;
        do {
            result = queue.take();
            // 随机超时时间
            long timeout = random.nextInt(100);
            // 模拟等待时间，RPC 或者 DB 查询
            Thread.sleep(timeout);
            // 计算结果
            result.setResult("Hello World");
            println("执行计算结果，消耗" + timeout + " ms. ");
        } while (result != null);
    }


    @GetMapping("hello-world")
    public DeferredResult<String> helloWorld() {
        DeferredResult<String> result = new DeferredResult<>(50L);
        // result.setResult("Hello World");
        // 入队操作
        queue.offer(result);
        println("Hello World");
        result.onCompletion(() -> {
            println("执行结束");
        });
        result.onTimeout(() -> {
            println("执行超时");
        });
        return result;
    }

    private static void println(Object object) {
        String threadName = Thread.currentThread().getName();
        System.out.println("HelloWorldAsyncController [" + threadName + "]：" + object);
    }

}
