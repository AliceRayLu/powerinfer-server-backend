package com.powerinfer.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.powerinfer.server.utils.AddreessManager;
import com.powerinfer.server.utils.enums;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

@TableName("tasks")
public class Task {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String tid; // type id
    private String dir; // the absolute path
    @EnumValue
    @JsonProperty("state")
    private enums.TaskState state;
    @JsonProperty("version")
    private String version;
    @JsonProperty("created")
    private LocalDateTime created;
    @JsonProperty("started")
    private LocalDateTime started;
    @JsonProperty("finished")
    private LocalDateTime finished;
    @TableField(exist = false)
    @JsonProperty("queue")
    private int queue;
    @TableField(exist = false)
    @JsonProperty("progress")
    private int progress;
    @TableField(exist = false)
    @JsonProperty("waitingTime")
    private long waitingTime; // minutes
    @TableField(exist = false)
    @JsonProperty("runningTime")
    private long runningTime; // minutes

    public Task(String tid, String dir, String version) {
        this.tid = tid;
        this.dir = dir;
        this.state = enums.TaskState.UPLOADING;
        this.version = version;
    }

    public enums.TaskState getState() { return state; }
    public void setState(enums.TaskState state) { this.state = state; }
    public String getTid() { return tid; }
    public String getDir() { return dir; }
    public String getVersion() { return version; }
    public String getId() { return id; }
    public LocalDateTime getCreated() { return created; }

    public void setCreated() { this.created = LocalDateTime.now(); }
    public void setStarted() { this.started = LocalDateTime.now(); }
    public void setFinished() { this.finished = LocalDateTime.now(); }
    public void setQueue(int queue) { this.queue = queue; }
    public void setProgress(int progress) { this.progress = progress; }
    public void setWaitingTime(long waitingTime) { this.waitingTime = waitingTime; }
    public void setRunningTime(long runningTime) { this.runningTime = runningTime; }

    public int execute() throws IOException, InterruptedException {
        System.out.println("==============> Starting to execute task " + tid);
        ProcessBuilder pb = new ProcessBuilder("python", AddreessManager.getTrainPythonPath(), dir);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        if (!p.isAlive()) {
            throw new IOException("Failed to start Python process");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[Task " + tid + "] " + line);
            }
        }
        return p.waitFor();
    }


    public void cleanUp() {
        // TODO: clean up dirs
        File folder = new File(dir);
        AddreessManager.deleteDir(folder);
        folder.getParentFile().delete();
    }
}
