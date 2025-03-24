package com.powerinfer.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("types")
public class Type {
    @TableId(type = IdType.ASSIGN_UUID)
    private String tid;
    @JsonProperty("name")
    private String name;
    @JsonProperty("version")
    private String version;
    @JsonProperty("size")
    private long size;
    private String mid;
    // TODO: files
    @JsonProperty("dir")
    private String dir; // directory of model files in server
    @JsonProperty("dir_info")
    private String dir_info; // serialize as json format
    @JsonProperty("size_info")
    private String size_info; // record the size of a file
}
