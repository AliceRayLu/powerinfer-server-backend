package com.powerinfer.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class UserController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/hello")
    public void hello(){
        System.out.println("Hello!");
    }
}
