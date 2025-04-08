package com.powerinfer.server.controller;

import com.powerinfer.server.entity.Model;
import com.powerinfer.server.entity.Type;
import com.powerinfer.server.entity.User;
import com.powerinfer.server.service.ModelService;
import com.powerinfer.server.service.TaskService;
import com.powerinfer.server.service.TypeService;
import com.powerinfer.server.service.UserService;
import com.powerinfer.server.utils.enums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/model")
public class ModelController {
    @Autowired
    private ModelService modelService;
    @Autowired
    private UserService userService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TypeService typeService;

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

    @PostMapping({"/usr/get/own"})
    public ArrayList<Model> getOwn(String uid, String search){
        // get all the models belongs to one user
        return null;
    }

    @PostMapping("/detail")
    public ResponseEntity<Model> getDetail(@RequestParam String mname, @RequestParam String uname, @RequestParam String uid){
        // get the readme and other info about a specific model
        User owner = userService.getUserByUsername(uname);
        Model model = modelService.getModel(mname, owner.getUid());
        if(model == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(!owner.getUid().equals(uid) && model.getVisibility() != enums.Visibility.PUBLIC) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @PostMapping("/new")
    public boolean addModel(@RequestBody Model model){
        Model temp = modelService.getModel(model.getName(), model.getUid());
        if (temp != null) {
            return false;
        }
        modelService.save(model);
        return true;
    }

    @PostMapping("/remove")
    public void removeModel(@RequestParam String mid){
        List<String> tids = typeService.getAllTypeIds(mid);
        for (String tid : tids) {
            taskService.removeTasksByTid(tid);
        }
        typeService.removeTypesByMid(mid);
        modelService.removeById(mid);
    }

    @PostMapping("/switch")
    public void switchVisibility(@RequestParam String mid){
        Model model = modelService.getById(mid);
        if(model.getVisibility() == enums.Visibility.PUBLIC) {
           model.setVisibility(enums.Visibility.PRIVATE);
        }else{
            model.setVisibility(enums.Visibility.PUBLIC);
        }
        modelService.updateById(model);
    }
}
