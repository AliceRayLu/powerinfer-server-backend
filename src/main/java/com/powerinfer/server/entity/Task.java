package com.powerinfer.server.entity;

import com.powerinfer.server.utils.AddreessManager;
import com.powerinfer.server.utils.enums;
import org.springframework.scheduling.annotation.Async;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class Task {
    private String tid; // type id
    private String dir;
    private enums.TaskState state;

    public Task(String tid, String dir) {
        this.tid = tid;
        this.dir = dir;
        this.state = enums.TaskState.QUEUED;
    }

    public enums.TaskState getState() { return state; }
    public void setState(enums.TaskState state) { this.state = state; }
    public String getTid() { return tid; }

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
