package com.yafey.web.controller.demo4handlerException;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice	// 标识 这是一个 处理 Controller 异常 的类，并以 JSON 格式返回。
public class ControllerExceptionHandler {

    @ExceptionHandler(UserException4Demo.class)		// 标识 需要处理的 异常类型
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleUserNotExistException(UserException4Demo e) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", e.getId());
        result.put("message", e.getMessage());
        return result;
    }

}
