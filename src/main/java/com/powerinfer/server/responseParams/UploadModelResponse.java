package com.powerinfer.server.responseParams;

import com.powerinfer.server.requestParams.UploadModelRequest;
import com.powerinfer.server.utils.enums;

public class UploadModelResponse {
    private String msg;
    private enums.UploadAuthState authState;

    public UploadModelResponse(String msg, enums.UploadAuthState authState) {
        this.msg = msg;
        this.authState = authState;
    }

    public String getMsg() { return msg; }
    public enums.UploadAuthState getAuthState() { return authState; }
}
