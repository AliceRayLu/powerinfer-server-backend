package com.powerinfer.server.utils;

public class enums {
    public enum KeyType {
        ssh,
        hf,
    }

    public enum Visibility {
        PUBLIC,
        PRIVATE,
    }

    public enum GetModelState {
        SUCCESS,
        MODEL_NOT_FOUND,
        TYPE_NOT_FOUND,
        MODEL_UNACCESSIBLE,
    }

    public enum TaskState {
        SUCCESS,
        QUEUED,
        RUNNING,
        FAILED,
    }

    public enum UploadAuthState {
        ALLOWED,
        DENIED,
        NEEDS_CANCEL
    }
}
