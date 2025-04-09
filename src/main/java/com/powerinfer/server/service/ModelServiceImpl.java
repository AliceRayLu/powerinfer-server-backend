package com.powerinfer.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powerinfer.server.entity.Model;
import com.powerinfer.server.mapper.ModelMapper;
import com.powerinfer.server.utils.enums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

@Service
public class ModelServiceImpl extends ServiceImpl<ModelMapper, Model> implements ModelService{
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Model getModel(String name, String uid) {
        return modelMapper.selectOne(new QueryWrapper<Model>().eq("name", name).eq("uid", uid));
    }

    @Override
    public Page<Model> getAllPublicModels(String keyword, Pageable pageable, String sortBy) {
        QueryWrapper<Model> queryWrapper = new QueryWrapper<Model>()
                .eq("visibility", enums.Visibility.PUBLIC).orderByDesc(sortBy);
        if(keyword != null && !keyword.isEmpty()){
            queryWrapper = queryWrapper.like("name", keyword);
        }
        return modelMapper.selectPage(new Page<>(pageable.getPageNumber(), pageable.getPageSize()), queryWrapper);
    }

    @Override
    public Page<Model> getUserModels(String uid, String keyword, Pageable pageable, boolean isPub, String sortBy){
        QueryWrapper<Model> queryWrapper = new QueryWrapper<Model>()
                .eq("uid", uid).orderByDesc(sortBy);
        if(isPub) {
            queryWrapper = queryWrapper.eq("visibility", enums.Visibility.PUBLIC);
        }
        if(keyword != null && !keyword.isEmpty()){
            queryWrapper = queryWrapper.like("name", keyword);
        }
        return modelMapper.selectPage(new Page<>(pageable.getPageNumber(), pageable.getPageSize()), queryWrapper);
    }
}
