package com.powerinfer.server.requestParams;

public class ChangePasswdRequest {
    private String uid;
    private String old_passwd;
    private String new_passwd;

    public String getUid() {return uid;}
    public String getOld_passwd() {return old_passwd;}
    public String getNew_passwd() {return new_passwd;}
}
