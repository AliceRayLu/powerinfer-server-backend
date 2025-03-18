package com.powerinfer.server.controller;

import com.powerinfer.server.entity.Key;
import com.powerinfer.server.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/key")
public class KeyController {
    @Autowired
    private KeyService keyService;

    @PostMapping("/add")
    public boolean addKey(@RequestBody Key key){
        Key temp = keyService.getKeyByContent(key.getContent());
        if(temp != null){return false;}
        System.out.println(key.getType());
        keyService.save(key);
        // TODO: add into server file (if use ssh to transmit file)
        return true;
    }

    @PostMapping("/delete")
    public void deleteKey(@RequestBody Key key){
        keyService.removeById(key.getKid());
    }

    @PostMapping("/get/usr/ssh")
    public List<Key> getSSHKeys(@RequestParam String uid){
        return keyService.getSSHListOfUser(uid);
    }

    @PostMapping("/get/usr/hf")
    public List<Key> getHFKeys(@RequestParam String uid){
        return keyService.getHfListOfUser(uid);
    }
}
