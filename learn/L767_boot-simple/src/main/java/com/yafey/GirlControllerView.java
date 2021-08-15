package com.yafey;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GirlControllerView {

    @GetMapping("/index")
    public String index() {
        return "index";
    }
    
}
