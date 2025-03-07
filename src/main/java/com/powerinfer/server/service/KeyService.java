package com.powerinfer.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.powerinfer.server.entity.Key;

import java.util.List;

public interface KeyService extends IService<Key> {
    Key getKeyByContent(String content);
    List<Key> getSSHListOfUser(String uid);
    List<Key> getHfListOfUser(String uid);
}
