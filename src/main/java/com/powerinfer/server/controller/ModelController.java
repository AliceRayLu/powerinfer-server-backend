package com.powerinfer.server.controller;

import com.powerinfer.server.entity.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@CrossOrigin
@RequestMapping("/model")
public class ModelController {
    @PostMapping("/pub/get")
    public ArrayList<Model> getPub(String search){
        // get all the public model ranks
        return null;
    }

    @PostMapping("/usr/get")
    public ArrayList<Model> getUser(String uid, boolean auth,String search){
        // get all the models belong to one specific user
        return null;
    }

    @PostMapping("/detail")
    public Model getDetail(String name){
        // get the readme and other info about a specific model
        return null;
    }
}
