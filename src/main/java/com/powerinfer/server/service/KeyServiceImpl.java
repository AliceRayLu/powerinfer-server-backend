package com.powerinfer.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powerinfer.server.entity.Key;
import com.powerinfer.server.mapper.KeyMapper;
import com.powerinfer.server.utils.enums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeyServiceImpl extends ServiceImpl<KeyMapper, Key> implements KeyService {
    @Autowired
    private KeyMapper keyMapper;

    @Override
    public Key getKeyByContent(String content) {
        return keyMapper.selectOne(new QueryWrapper<Key>().eq("content", content));
    }

    @Override
    public List<Key> getSSHListOfUser(String uid){
        return keyMapper.selectList(new QueryWrapper<Key>().eq("uid", uid).eq("type", enums.KeyType.ssh));
    }

    @Override
    public List<Key> getHfListOfUser(String uid){
        return keyMapper.selectList(new QueryWrapper<Key>().eq("uid", uid).eq("type", enums.KeyType.hf));
    }
}
