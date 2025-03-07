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

    @PostMapping("/getUser")
    public String connect(@RequestBody String pub_key){
        Key key = keyService.getKeyByContent(pub_key);
        if(key != null){
            return key.getUid();
        }
        return "";
    }

    @PostMapping("/add")
    public boolean addKey(@RequestBody String content,@RequestBody String uid,@RequestBody boolean isHF){
        Key temp = keyService.getKeyByContent(content);
        if(temp != null){return false;}
        Key key = new Key(content,uid,isHF);
        keyService.save(key);
        // TODO: add into server file
        return true;
    }

    @PostMapping("/delete")
    public void deleteKey(@RequestBody String kid){
        keyService.removeById(kid);
    }

    @PostMapping("/get/usr/ssh")
    public List<Key> getSSHKeys(@RequestBody String uid){
        return keyService.getSSHListOfUser(uid);
    }

    @PostMapping("/get/usr/hf")
    public List<Key> getHFKeys(@RequestBody String uid){
        return keyService.getHfListOfUser(uid);
    }
}
