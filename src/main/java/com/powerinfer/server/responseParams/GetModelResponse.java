package com.powerinfer.server.responseParams;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.powerinfer.server.utils.enums;
import com.powerinfer.server.entity.Type;

public class GetModelResponse {
    @JsonProperty("state")
    private enums.GetModelState state;
    @JsonProperty("model")
    private Type modelType;
    @JsonProperty("message")
    private String message;

    public GetModelResponse(enums.GetModelState state) {
        this.state = state;
    }

    public void setState(enums.GetModelState state) {this.state = state;}
    public void setModelType(Type modelType) {this.modelType = modelType;}
    public void setMessage(String message) {this.message = message;}

    public Type getModelType() {return modelType;}
}
