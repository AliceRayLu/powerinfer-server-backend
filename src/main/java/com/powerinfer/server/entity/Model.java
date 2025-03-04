package com.powerinfer.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.powerinfer.server.utils;

import java.util.Date;

@TableName("model")
public class Model {
    @TableId(type = IdType.AUTO)
    private String mid;
    private utils.Visibility visibility;
    private int num_down; // number of download times
    private Date date; // last updated time
    private String uid;
    private utils.Arch arch;
    // TODO: how to store readme

}
