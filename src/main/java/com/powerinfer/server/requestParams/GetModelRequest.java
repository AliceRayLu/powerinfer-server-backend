package com.powerinfer.server.requestParams;

public class GetModelRequest {
    private String mname;
    private String tname;
    private String uname; // Who does the model belong

    public GetModelRequest(){
        this.mname = null;
        this.tname = null;
        this.uname = null;
    }

    public GetModelRequest(String mname, String tname){
        this.mname = mname;
        this.tname = tname;
        this.uname = null;
    }


    public GetModelRequest(String mname, String tname, String uname){
        this.mname = mname;
        this.tname = tname;
        this.uname = uname;
    }

    public String getMname() {return mname;}
    public String getTname() {return tname;}
    public String getUname() {return uname;}
}
