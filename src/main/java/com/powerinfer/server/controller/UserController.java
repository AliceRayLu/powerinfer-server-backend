package com.powerinfer.server.controller;

import com.powerinfer.server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/hello")
    public void hello(){
        System.out.println("Hello!");
    }

    @GetMapping("/login")
    public void login(String uname, String passwd){
        //TODO
    }

    @GetMapping("/register")
    public void register(String uname, String passwd, String email){}
}
