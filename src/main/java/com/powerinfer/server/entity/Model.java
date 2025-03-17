package com.powerinfer.server.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.powerinfer.server.utils.enums;

import java.util.Date;

@TableName("models")
public class Model {
    @TableId(type = IdType.ASSIGN_UUID)
    private String mid;
    private String name;
    @EnumValue
    private enums.Visibility visibility;
    private int num_down; // number of download times
    private Date date; // last updated time
    private String uid;
    @EnumValue
    private enums.Arch arch;

    private String md; // markdown files
}
