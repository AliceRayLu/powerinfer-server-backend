package com.powerinfer.server.requestParams;

public class UploadModelRequest {
    private String mname;
    private String tname;
    private String fname;
    private String md5;
    private boolean need_train;

    public String getMname() { return mname; }
    public String getTname() { return tname; }
    public String getFname() { return fname; }
    public String getMd5() { return md5; }
    public boolean isNeed_train() { return need_train; }
}
