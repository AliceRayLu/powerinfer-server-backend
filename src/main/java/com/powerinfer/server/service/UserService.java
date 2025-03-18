package com.powerinfer.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.powerinfer.server.entity.User;

public interface UserService extends IService<User> {
    User getUserByEmail(String email);
    User getUserByUsername(String username);
}
