package com.powerinfer.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.powerinfer.server.utils;

@TableName("key")
public class Key {
    @TableId(type = IdType.AUTO)
    private String kid;
    private String content;
    private String uid;
    private utils.KeyType type;

    public Key() {}
    public Key(String content, String uid, boolean isHF) {
        this.content = content;
        this.uid = uid;
        this.type = isHF ? utils.KeyType.hf : utils.KeyType.ssh;
    }

    public String getKid() {return kid;}
    public void setKid(String id) { this.kid = id;}
    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}
    public String getUid() {return uid;}
    public void setUid(String uid) { this.uid = uid;}
    public utils.KeyType getType() {return type;}
    public void setType(utils.KeyType type) {this.type = type;}
}
