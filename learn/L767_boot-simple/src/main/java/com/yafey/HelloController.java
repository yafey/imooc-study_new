package com.yafey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@RestController
@Data
public class HelloController {
    
    @Value("${com.yafey.name}")
    private  String name;
    @Value("${com.yafey.age}")
    private  Integer age;
    
    @GetMapping(value="/hello",produces="application/json;charset=UTF-8")
    public String say() {
        return "Hello Spring Boot!"+name+","+age;
    }

}
