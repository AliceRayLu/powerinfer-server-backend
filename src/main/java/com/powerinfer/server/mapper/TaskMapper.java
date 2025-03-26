package com.powerinfer.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.powerinfer.server.entity.Task;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}
