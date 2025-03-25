package com.powerinfer.server.service;

import com.powerinfer.server.entity.Task;
import com.powerinfer.server.utils.enums;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

@Service
public class TaskService {
    private static class TaskQueue {
        private static Queue<Task> taskQueue = new LinkedList<Task>();
        private static Task currentTask = null;

        private TaskQueue() {}

        public static synchronized void add(Task task) {
            System.out.println("=============> Adding task: " + task.getTid());
            taskQueue.add(task);
        }

        public static synchronized Task get() {
            if (currentTask == null && !taskQueue.isEmpty()) {
                currentTask = taskQueue.poll();
            }
            return currentTask;
        }

        public static void complete() {
            currentTask = null;
            System.out.println("=============> Task completed");
        }

        public static int getPos(String tid) {
            if (currentTask != null && currentTask.getTid().equals(tid)) {
                return 0;
            }
            int pos = 1;
            for (Task task : taskQueue) {
                if (task.getTid().equals(tid)) {
                    return pos;
                }
                pos++;
            }
            return -1;
        }
    }

    @Scheduled(fixedDelay = 1000)
    @Async
    public void executeTasks(){
        Task task = TaskQueue.get();
        if (task != null) {
            task.setState(enums.TaskState.RUNNING);
            try {
                int exitCode = task.execute();
                // TODO: clean up and store into db
            } catch (Exception e) {
                task.setState(enums.TaskState.FAILED);
                System.out.println("Task failed " + e.getMessage());
            } finally {
                TaskQueue.complete();
            }
        }
    }

    public void addTask(String tid, String dir){
        Task task = new Task(tid, dir);
        TaskQueue.add(task);
    }

    public int checkPos(String tid) {
        return TaskQueue.getPos(tid);
    }
}
