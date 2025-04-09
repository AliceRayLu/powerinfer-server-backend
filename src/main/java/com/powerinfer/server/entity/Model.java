package com.powerinfer.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String types; // use , to separate different types
    @TableField(exist = false)
    @JsonProperty("uname")
    private String uname;

    public Model(){
        this.visibility = enums.Visibility.PUBLIC;
        this.numDown = 0;
        this.date = new Date();
        this.types = "";
    }
    public Model(String name, String uid, enums.Visibility visibility){
        this.name = name;
        this.uid = uid;
        this.visibility = visibility;
        this.numDown = 0;
        this.date = new Date();
        this.types = "";
    }

    public enums.Visibility getVisibility() {
        return visibility;
    }
    public void setVisibility(enums.Visibility visibility) {
        this.visibility = visibility;
    }
    public String getMid() { return mid;}
    public String getName() { return name;}
    public int getNumDown() { return numDown;}
    public Date getDate() { return date;}
    public String getUid() { return uid;}
    public void update(String size){
        this.date = new Date();
        String[] sizes = types.split(",");
        boolean has = false;
        for(String s: sizes){
            if(s.equals(size)){
                has = true;
                break;
            }
        }
        if(!has){
            types = types + "," + size;
        }
    }
    public void addDown(){
        this.numDown++;
    }
    public void setTypes(String types){ this.types = types; }
    public String getTypes() {
        return types;
    }

    public void removeType(String type){
        if(types == null || types.isEmpty()) return;
        String[] all = types.split(",");
        StringBuilder sb = new StringBuilder();
        for(String s : all){
            if(!s.equals(type)){
                if(!sb.isEmpty()) sb.append(",");
                sb.append(s);
            }
        }
        types = sb.toString();
    }
    public void addType(String type){
        if(types == null || types.isEmpty()){
            types = type;
            return;
        }
        String[] all = types.split(",");
        boolean contains = false;
        for(String s : all){
            if(s.equals(type)){
                contains = true;
                break;
            }
        }
        if(!contains){
            types = types + "," + type;
        }
    }

    public void setUname(String uname) {
        this.uname = uname;
    }
}
