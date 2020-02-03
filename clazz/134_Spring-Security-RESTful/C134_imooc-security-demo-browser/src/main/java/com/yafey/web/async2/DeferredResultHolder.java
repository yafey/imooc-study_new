package com.yafey.web.async2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import lombok.Data;

@Data
@Component
public class DeferredResultHolder {
	// 以订单号为 key ，DeferredResult 对象为 值。
	private Map<String, DeferredResult<String>> map = new HashMap<>();
}
