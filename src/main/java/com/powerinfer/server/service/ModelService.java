package com.powerinfer.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.powerinfer.server.entity.Model;

public interface ModelService extends IService<Model> {
    Model getModel(String name, String uid);
}
