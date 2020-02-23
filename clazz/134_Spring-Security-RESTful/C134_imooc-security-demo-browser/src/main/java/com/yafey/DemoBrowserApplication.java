package com.yafey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@RestController
@EnableSwagger2
public class DemoBrowserApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoBrowserApplication.class, args);
	}
	
	@GetMapping("/")
	public String hello() {
		return "hello Spring Security";
	}
	
    @GetMapping("/me")
    public Object getCurrentUser(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
    
    @GetMapping("/me1")
    public Object getCurrentUser(Authentication authentication){
        return authentication;
    }
    
    @GetMapping("/me2")
    public Object getCurrentUser(@AuthenticationPrincipal UserDetails userDetails){
        return userDetails;
    }

}
