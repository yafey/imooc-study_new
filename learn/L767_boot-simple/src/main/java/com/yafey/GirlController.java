package com.yafey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@RestController
@Data
@RequestMapping("/hi")
public class GirlController {
    @Autowired
    private GirlProperties girlProperties;
    
    @Value("${content}")
    private String content;
    
    @Value("${price}")
    private int price;
    
    @GetMapping(value= {"/say","/price"})
    public String say() {
        return "content:"+content +", price:"+price;
    }
    
    @GetMapping("/say2")
    public String say2() {
        return "girlProperties:"+girlProperties;
    }
    
    @RequestMapping("/say3/{id}")
    public String say3(@PathVariable("id") Integer myId) {
        return "id:" + myId;
    }
    
    // url : /say4?id=111  , id 不填默认为 0 。
    @GetMapping("/say4")
    public String say4(@RequestParam(value = "id", required = false, defaultValue = "0") Integer myId) {
        return "id:" + myId;
    }
    


}
