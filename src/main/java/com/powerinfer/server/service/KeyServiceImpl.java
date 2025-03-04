package com.powerinfer.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powerinfer.server.entity.Key;
import com.powerinfer.server.mapper.KeyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeyServiceImpl extends ServiceImpl<KeyMapper, Key> implements KeyService {
    @Autowired
    private KeyMapper keyMapper;

    @Override
    public Key getKeyByContent(String content) {
        Key key = keyMapper.selectOne(new QueryWrapper<Key>().eq("content", content));
        return key;
    }
}
