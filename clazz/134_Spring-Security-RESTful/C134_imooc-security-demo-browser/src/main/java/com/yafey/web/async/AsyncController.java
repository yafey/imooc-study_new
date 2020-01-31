package com.yafey.web.async;

import java.util.concurrent.Callable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AsyncController {

    @RequestMapping("/order")
    public Callable<String> order() throws InterruptedException {
        Thread.currentThread().setName("主线程");
    	log.info("主线程开始");
        
        Callable<String> result = () -> {
        	Thread.currentThread().setName("副线程");
        	log.info("副线程开始");
            Thread.sleep(1000);
            log.info("副线程返回");
            return null;
        };

        log.info("主线程返回");
        return result;
    }
}
