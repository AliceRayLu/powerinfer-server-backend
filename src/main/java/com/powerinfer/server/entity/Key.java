package com.powerinfer.server.entity;

import com.powerinfer.server.utils;

public class Key {
    private String kid;
    private String content;
    private String mid;
    private utils.KeyType type;

    public String getKid() {return kid;}
    public void setKid(String id) { this.kid = id;}
    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}
    public String getMid() {return mid;}
    public void setMid(String mid) { this.mid = mid;}
    public utils.KeyType getType() {return type;}
    public void setType(utils.KeyType type) {this.type = type;}
}
