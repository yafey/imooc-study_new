package com.yafey.web.async2;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;


/**
 * 作用： 监听 模拟消息队列中 completedOrder 的值，然后通过 DeferredResultHolder， 返回一个 DeferredResult 对象 给前端。 
 * @author YaFey
 *
 */
// ContextRefreshedEvent ： Spring 容器初始化完毕 之后的事件。
@Component
@Slf4j
public class QueueListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private MockQueue mockQueue;
 
    @Autowired
    private DeferredResultHolder deferredResultHolder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
    	// 无限循环，使用一个新的线程，以防线程阻塞。
        new Thread(() -> {
            while (true) {
                if (StringUtils.isNotBlank(mockQueue.getCompletedOrder())) {

                    String orderNumber = mockQueue.getCompletedOrder();
                    log.info("返回订单处理结果: {}", orderNumber);
                    deferredResultHolder.getMap().get(orderNumber).setResult("place order success");
                    mockQueue.setCompletedOrder(null); // 将模拟队列中的 完成的消息 移除。

                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }

        }).start();

    }
}
