package com.powerinfer.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powerinfer.server.entity.Type;
import com.powerinfer.server.mapper.TypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements TypeService {

    @Autowired
    private TypeMapper typeMapper;

    @Override
    public List<String> getAllTypeIds(String mid){
        return typeMapper.selectObjs(new QueryWrapper<Type>()
                .eq("mid", mid)
                .select("tid"))
                .stream()
                .map(obj -> (String) obj)
                .toList();
    }

    @Override
    public Type getTypeByMidAndName(String mid, String name){
        return typeMapper.selectOne(
                new QueryWrapper<Type>()
                        .eq("mid", mid)
                        .eq("name", name));
    }

    @Override
    public void removeTypesByMid(String mid) {
        typeMapper.delete(new QueryWrapper<Type>().eq("mid", mid));
    }
}
