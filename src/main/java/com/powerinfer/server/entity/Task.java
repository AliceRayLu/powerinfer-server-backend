package com.powerinfer.server.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.powerinfer.server.utils.AddreessManager;
import com.powerinfer.server.utils.enums;

import java.io.BufferedReader;
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
    private enums.TaskState state;
    private String version;
    private LocalDateTime created;
    private LocalDateTime started;
    private LocalDateTime finished;

    public Task(String tid, String dir, String version) {
        this.tid = tid;
        this.dir = dir;
        this.state = enums.TaskState.QUEUED;
        this.version = version;
    }

    public enums.TaskState getState() { return state; }
    public void setState(enums.TaskState state) { this.state = state; }
    public String getTid() { return tid; }
    public String getDir() { return dir; }
    public String getVersion() { return version; }

    public void setCreated() { this.created = LocalDateTime.now(); }
    public void setStarted() { this.started = LocalDateTime.now(); }
    public void setFinished() { this.finished = LocalDateTime.now(); }

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
}
