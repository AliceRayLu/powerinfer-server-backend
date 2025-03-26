package com.powerinfer.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.powerinfer.server.entity.Task;
import com.powerinfer.server.requestParams.UploadModelRequest;
import com.powerinfer.server.responseParams.UploadModelResponse;
import com.powerinfer.server.utils.enums;

public interface TaskService extends IService<Task> {
    void addTask(String tid, String dir, String version);
    int checkPos(String tid);
    UploadModelResponse checkAuth(String uid, String dir_name);
}
