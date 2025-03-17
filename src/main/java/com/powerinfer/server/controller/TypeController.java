package com.powerinfer.server.controller;

import com.powerinfer.server.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/type")
public class TypeController {
    @Autowired
    private ModelService modelService;

    @PostMapping({"/download","/client/download"})
    public String getDownload(String mname, String mid, String tname){
        return "";
    }

    @PostMapping({"/remove", "/client/remove"})
    public void remove(){

    }

    @PostMapping("/client/update")
    public void updateType(String mname, String uid, String file){

    }
}
