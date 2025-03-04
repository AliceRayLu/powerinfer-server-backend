package com.powerinfer.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.powerinfer.server.entity.Key;

public interface KeyService extends IService<Key> {
    Key getKeyByContent(String content);
}
