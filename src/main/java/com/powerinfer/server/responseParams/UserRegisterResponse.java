package com.powerinfer.server.responseParams;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserRegisterResponse {
    @JsonProperty("state")
    private boolean success;
    @JsonProperty("username")
    private boolean username;
    @JsonProperty("email")
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
