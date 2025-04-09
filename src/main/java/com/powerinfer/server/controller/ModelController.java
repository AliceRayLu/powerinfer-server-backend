package com.powerinfer.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powerinfer.server.entity.Model;
import com.powerinfer.server.entity.User;
import com.powerinfer.server.requestParams.PagedRequest;
import com.powerinfer.server.service.ModelService;
import com.powerinfer.server.service.TaskService;
import com.powerinfer.server.service.TypeService;
import com.powerinfer.server.service.UserService;
import com.powerinfer.server.utils.enums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping(value = "/pub/get",produces = "application/json")
    public ResponseEntity<Page<Model>> getPub(@RequestBody PagedRequest pageConfig) {
        Pageable pageable = PageRequest.of(pageConfig.getPage(), pageConfig.getSize());
        Page<Model> models = modelService.getAllPublicModels(pageConfig.getSearch(), pageable, pageConfig.getSortBy());
        for(Model model: models.getRecords()) {
            User user = userService.getById(model.getUid());
            if(user != null) {
                model.setUname(user.getName());
            }
        }
        return new ResponseEntity<>(models, HttpStatus.OK);
    }

    @PostMapping(value = "/usr/get",produces = "application/json")
    public ResponseEntity<Page<Model>> getUser(@RequestBody PagedRequest pageConfig) {
        User user = userService.getUserByUsername(pageConfig.getUser());
        Pageable pageable = PageRequest.of(pageConfig.getPage(), pageConfig.getSize());
        Page<Model> models = modelService.getUserModels(
                user.getUid(), pageConfig.getSearch(), pageable, true, pageConfig.getSortBy()
        );
        return new ResponseEntity<>(models, HttpStatus.OK);
    }

    @PostMapping(value ="/usr/get/own", produces = "application/json")
    public ResponseEntity<Page<Model>> getOwn(@RequestParam PagedRequest pageConfig) {
        Pageable pageable = PageRequest.of(pageConfig.getPage(), pageConfig.getSize());
        Page<Model> models = modelService.getUserModels(
                pageConfig.getUser(), pageConfig.getSearch(), pageable, false, pageConfig.getSortBy()
        );
        return new ResponseEntity<>(models, HttpStatus.OK);
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
