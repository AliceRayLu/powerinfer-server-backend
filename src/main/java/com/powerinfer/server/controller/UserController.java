package com.powerinfer.server.controller;

import com.powerinfer.server.entity.User;
import com.powerinfer.server.requestParams.PasswdParams;
import com.powerinfer.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/usr")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/hello")
    public void hello(){
        System.out.println("Hello!");
    }

    @PostMapping("/login")
    public String login(@RequestBody User user){
        User usr = userService.getUserByEmail(user.getEmail());
        if(usr == null){
            return "";
        }
        if(usr.getPasswd().equals(user.getPasswd())){
            return user.getUid();
        }
        return "wrong password";
    }

    @PostMapping("/getName")
    public String getName(@RequestParam String uid){
        User user = userService.getById(uid);
        return user.getName();
    }

    @PostMapping("/register")
    public boolean register(@RequestBody User user){
        User temp = userService.getUserByEmail(user.getEmail());
        if(temp != null) {return false;}
        userService.save(user);
        return true;
    }

    @PostMapping("/getInfo")
    public User getInfo(@RequestParam String uid){
        User user = userService.getById(uid);
        user.setPasswd(null);
        return user;
    }

    @PostMapping("/update")
    public boolean update(@RequestBody User user){
        User data = userService.getById(user.getUid());
        if(!user.getEmail().equals(data.getEmail())){
            User temp = userService.getUserByEmail(user.getEmail());
            if(temp != null) {
                return false;
            }
        }
        user.setPasswd(data.getPasswd());
        return userService.updateById(user);
    }

    @PostMapping("/passwd/update")
    public boolean updatePasswd(@RequestBody PasswdParams params){
        User user = userService.getById(params.getUid());
        if(!params.getOld_passwd().equals(user.getPasswd())){
            return false;
        }
        user.setPasswd(params.getNew_passwd());
        return userService.updateById(user);
    }
}
