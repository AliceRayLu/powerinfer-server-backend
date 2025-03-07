package com.powerinfer.server.controller;

import com.powerinfer.server.entity.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@CrossOrigin
@RequestMapping("/model")
public class ModelController {
    @GetMapping("/pub/get")
    public ArrayList<Model> getPub(String search){
        return null;
    }

    @GetMapping("/usr/get")
    public ArrayList<Model> getUser(String uid, boolean auth,String search){
        return null;
    }

    @GetMapping("/detail")
    public Model getDetail(String name){
        return null;
    }
}
