package com.powerinfer.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.powerinfer.server.entity.Model;
import org.springframework.data.domain.Pageable;


public interface ModelService extends IService<Model> {
    Model getModel(String name, String uid);
    Page<Model> getAllPublicModels(String keyword, Pageable pageable, String sortBy);
    Page<Model> getUserModels(String uid, String keyword, Pageable pageable, boolean isPub, String sortBy);
}
