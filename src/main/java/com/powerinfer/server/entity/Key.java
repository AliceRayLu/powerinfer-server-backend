package com.powerinfer.server.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.powerinfer.server.utils.enums;
import java.time.OffsetDateTime;

@TableName("pwi_keys")
public class Key {
    @TableId(type = IdType.ASSIGN_UUID)
    private String kid;
    private String content;
    private String uid;
    @JsonProperty("date")
    private OffsetDateTime date;
    @EnumValue
    private enums.KeyType type;

    public Key(){
        this.type = enums.KeyType.ssh;
    }

    public String getKid() {return kid;}
    public void setKid(String id) { this.kid = id;}
    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}
    public String getUid() {return uid;}
    public void setUid(String uid) { this.uid = uid;}
    public enums.KeyType getType() {return type;}
    public void setType(enums.KeyType type) {this.type = type;}
    public void setDate(){this.date = OffsetDateTime.now();}
}
