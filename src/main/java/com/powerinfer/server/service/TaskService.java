package com.powerinfer.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.powerinfer.server.entity.Task;
import com.powerinfer.server.responseParams.UploadModelResponse;
import reactor.core.publisher.Flux;

public interface TaskService extends IService<Task> {
    void addTask(String tid, String dir, String version);
    int checkPos(String tid);
    UploadModelResponse checkAuth(String uid, String dir_name);
    Flux<Task> findByTid(String tid);
    Task findLatestTaskByTid(String tid);
    long getWaitingMinutes(int pos);
    long getLeftMinutes(int progress);
    boolean cancelTask(String tid);
    void updateTask(Task task, boolean isRemove);
}
