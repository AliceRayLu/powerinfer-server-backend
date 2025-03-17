package com.powerinfer.server.controller;

import com.powerinfer.server.entity.Model;
import com.powerinfer.server.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@CrossOrigin
@RequestMapping("/model")
public class ModelController {
    @Autowired
    private ModelService modelService;

    @PostMapping("/pub/get")
    public ArrayList<Model> getPub(String search){
        // get all the public model ranks
        return null;
    }

    @PostMapping("/usr/get")
    public ArrayList<Model> getUser(String uid, String search){
        // get all the pub models belong to one user
        return null;
    }

    @PostMapping({"/usr/get/own", "/client/get/own"})
    public ArrayList<Model> getOwn(String uid, String search){
        return null;
    }

    @PostMapping("/detail")
    public Model getDetail(String mid){
        // get the readme and other info about a specific model
        return null;
    }

    @PostMapping("/new")
    public void addModel(@RequestBody Model model){
        modelService.save(model);
    }
}
