package com.powerinfer.server.controller;

import com.powerinfer.server.entity.Key;
import com.powerinfer.server.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/key")
public class KeyController {
    @Autowired
    private KeyService keyService;

    @GetMapping("/getUser")
    public String connect(String pub_key){
        Key key = keyService.getKeyByContent(pub_key);
        if(key != null){
            return key.getUid();
        }
        return "";
    }

    @GetMapping("/add")
    public boolean addKey(String content, String uid, boolean isHF){
        Key temp = keyService.getKeyByContent(content);
        if(temp != null){return false;}
        Key key = new Key(content,uid,isHF);
        keyService.save(key);
        // TODO: add into server file
        return true;
    }

    @GetMapping("/delete")
    public void deleteKey(String kid){
        keyService.removeById(kid);
    }
}
