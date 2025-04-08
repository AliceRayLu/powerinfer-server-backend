package com.powerinfer.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.powerinfer.server.entity.Type;

import java.util.List;

public interface TypeService extends IService<Type> {
    List<String> getAllTypeIds(String mid);
    Type getTypeByMidAndName(String mid, String name);
    void removeTypesByMid(String mid);
}
