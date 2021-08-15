package com.yafey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("girl")
public class GirlProperties {
    
    private String cupSize;
    private int age;

}
