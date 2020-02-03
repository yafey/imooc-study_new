package com.yafey.web.async2;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Data
@Slf4j
// 模拟消息队列
public class MockQueue {

	/**
	 * 模拟 下单 消息
	 */
	private String placeOrder;
	/**
	 * 模拟订单完成消息
	 */
	private String completedOrder;
	
	public void setPlaceOrder(String placeOrder) throws Exception {
		// 单独开一个线程来模拟 app2 处理 下单逻辑。
		new Thread(() -> {
            log.info("接到下单请求,{}",placeOrder);
            try {
        		Thread.sleep(1000); // 延时 1s 模拟 处理下单请求
            } catch (InterruptedException e) {
            	log.error(e.getMessage(), e);
            }
            this.completedOrder = placeOrder; // 模拟订单完成 消息，注意这里是修改 completedOrder 的值。 
            log.info("下单请求处理完毕,{}", completedOrder);
        }).start();
	}
	
}
