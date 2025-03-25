package com.powerinfer.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.powerinfer.server.utils.enums;

import java.util.Date;

@TableName("models")
public class Model {
    @TableId(type = IdType.ASSIGN_UUID)
    private String mid;
    private String name;
    @EnumValue
    private enums.Visibility visibility;
    @TableField("numDown")
    private int numDown; // number of download times
    private Date date; // last updated time
    private String uid;

    private String md; // markdown files path

    public Model(){
        this.visibility = enums.Visibility.PUBLIC;
        this.numDown = 0;
        this.date = new Date();
    }
    public Model(String name, String uid, enums.Visibility visibility){
        this.name = name;
        this.uid = uid;
        this.visibility = visibility;
        this.numDown = 0;
        this.date = new Date();
    }

    public enums.Visibility getVisibility() {
        return visibility;
    }
    public String getMid() { return mid;}
    public String getName() { return name;}
    public int getNumDown() { return numDown;}
    public Date getDate() { return date;}
    public String getUid() { return uid;}
    public void update(){
        this.date = new Date();
    }
    public void addDown(){
        this.numDown++;
    }
}
