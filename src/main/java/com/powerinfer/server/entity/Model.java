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

    private String md; // markdown files

    public Model(){
        this.visibility = enums.Visibility.PUBLIC;
        this.num_down = 0;
        this.date = new Date();
    }
    public Model(String name, String uid, enums.Visibility visibility){
        this.name = name;
        this.uid = uid;
        this.visibility = visibility;
        this.num_down = 0;
        this.date = new Date();
    }

    public enums.Visibility getVisibility() {
        return visibility;
    }
    public String getMid() { return mid;}
    public String getName() { return name;}
    public int getNum_down() { return num_down;}
    public Date getDate() { return date;}
    public String getUid() { return uid;}
}
