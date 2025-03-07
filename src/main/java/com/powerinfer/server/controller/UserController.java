package com.powerinfer.server.controller;

import com.powerinfer.server.entity.User;
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
    public String login(String email, String passwd){
        User user = userService.getUserByEmail(email);
        if(user == null){
            return "";
        }
        if(user.getPasswd().equals(passwd)){
            return user.getUid();
        }
        return "wrong password";
    }

    @PostMapping("/getName")
    public String getName(String uid){
        User user = userService.getById(uid);
        return user.getName();
    }

    @PostMapping("/register")
    public boolean register(String uname, String passwd, String email){
        User temp = userService.getUserByEmail(email);
        if(temp != null) {return false;}
        User user = new User(uname, passwd, email);
        userService.save(user);
        return true;
    }

    @PostMapping("/getInfo")
    public User getInfo(String uid){
        User user = userService.getById(uid);
        user.setPasswd(null);
        return user;
    }

    @PostMapping("/update")
    public boolean update(String uid, String uname, String email, String bio){
        User user = userService.getById(uid);
        if(!email.equals(user.getEmail())){
            User temp = userService.getUserByEmail(email);
            if(temp != null) {
                return false;
            }
        }
        user.setBio(bio);
        user.setEmail(email);
        user.setName(uname);
        return userService.updateById(user);
    }

    @PostMapping("/passwd/update")
    public boolean updatePasswd(String uid, String old_passwd, String new_passwd){
        User user = userService.getById(uid);
        if(!old_passwd.equals(user.getPasswd())){
            return false;
        }
        user.setPasswd(new_passwd);
        return userService.updateById(user);
    }
}
