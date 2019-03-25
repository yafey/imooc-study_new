package com.yafey.diveinspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan(basePackages="com.yafey.diveinspringboot.web.servlet")
public class C252DiveInSpirngBoot2Application {

	public static void main(String[] args) {
		SpringApplication.run(C252DiveInSpirngBoot2Application.class, args);
	}

}
