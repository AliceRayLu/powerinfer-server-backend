package com.powerinfer.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("type")
public class Type {
    @TableId(type = IdType.AUTO)
    private String tid;
    private String name;
    private String version;
    private long size;
    // TODO: files
}
