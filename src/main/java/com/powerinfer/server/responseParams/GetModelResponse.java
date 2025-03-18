package com.powerinfer.server.responseParams;

import com.powerinfer.server.utils.enums;
import com.powerinfer.server.entity.Type;

public class GetModelResponse {
    private enums.GetModelState state;
    private Type modelType;
    private String message;

    public GetModelResponse(enums.GetModelState state) {
        this.state = state;
    }

    public void setState(enums.GetModelState state) {this.state = state;}
    public void setModelType(Type modelType) {this.modelType = modelType;}
    public void setMessage(String message) {this.message = message;}
}
