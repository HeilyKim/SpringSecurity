package com.vs.js.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {
    @GetMapping("user")
    public String user(Authentication authentication) {
        System.out.println("test");
        System.out.println(authentication.getPrincipal());
        return authentication.getName();
    }

}
