package com.powerinfer.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.powerinfer.server.entity.Model;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ModelMapper extends BaseMapper<Model> {
}
