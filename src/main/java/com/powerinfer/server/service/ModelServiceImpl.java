package com.powerinfer.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powerinfer.server.entity.Model;
import com.powerinfer.server.mapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModelServiceImpl extends ServiceImpl<ModelMapper, Model> implements ModelService{
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Model getModel(String name, String uid) {
        return modelMapper.selectOne(new QueryWrapper<Model>().eq("name", name).eq("uid", uid));
    }
}
