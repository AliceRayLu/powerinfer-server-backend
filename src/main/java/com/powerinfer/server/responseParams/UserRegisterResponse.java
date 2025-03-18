package com.powerinfer.server.responseParams;

public class UserRegisterResponse {
    private boolean success;
    private boolean username;
    private boolean email;

    public UserRegisterResponse(boolean success, boolean username, boolean email) {
        this.success = success;
        this.username = username;
        this.email = email;
    }

    public void setSuccess() {success = false;}
    public void setUsername() {username = false;}
    public void setEmail() {email = false;}
    public boolean getSuccess() {return success;}

}
