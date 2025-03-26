package com.powerinfer.server.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powerinfer.server.entity.Model;
import com.powerinfer.server.entity.Task;
import com.powerinfer.server.entity.Type;
import com.powerinfer.server.mapper.TaskMapper;
import com.powerinfer.server.responseParams.UploadModelResponse;
import com.powerinfer.server.utils.AddreessManager;
import com.powerinfer.server.utils.enums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TypeService typeService;
    @Autowired
    private ModelService modelService;

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

        public static boolean inQueue(String dir) {
            if (currentTask != null && currentTask.getDir().equals(dir)){
                return true;
            }
            for(Task task : taskQueue) {
                if (task.getDir().equals(dir)) {
                    return true;
                }
            }
            return false;
        }
    }

    private void updateType(Task task){
        Type type = typeService.getById(task.getTid());
        assert type != null;
        type.updateVersion(task.getVersion());
        type.updateDir(task.getDir());
        typeService.updateById(type);
        Model model =  modelService.getById(type.getMid());
        assert model != null;
        model.update();
        modelService.updateById(model);
    }

    @Scheduled(fixedDelay = 1000)
    @Async
    public void executeTasks(){
        Task task = TaskQueue.get();
        if (task != null) {
            task.setState(enums.TaskState.RUNNING);
            task.setStarted();
            try {
                int exitCode = task.execute();
                // TODO: clean up and store into db
                task.setState(enums.TaskState.SUCCESS);
                updateType(task);
            } catch (Exception e) {
                task.setState(enums.TaskState.FAILED);
                System.out.println("Task failed " + e.getMessage());
            } finally {
                task.setFinished();
                TaskQueue.complete();
            }
        }
    }

    @Override
    public void addTask(String tid, String dir, String version){
        Task task = new Task(tid, dir, version);
        task.setCreated();
        TaskQueue.add(task);
        this.save(task);
    }

    @Override
    public int checkPos(String tid) {
        return TaskQueue.getPos(tid);
    }

    public UploadModelResponse checkAuth(String uid, String dir_name) {
        // step 1: check if user train dir exists
        File file = new File(AddreessManager.getTrainPythonPath() + uid);
        // step 2: if not, check train dir size; if size is 10, check oldest not in queue cache
        if(!file.exists()){
            File[] caches = new File(AddreessManager.getTrainPythonPath()).listFiles();
            File delete = null;
            long oldest = Long.MAX_VALUE;
            if(caches != null && caches.length >= 10) {
                for (File user_dir : caches) {
                    File[] models = user_dir.listFiles();
                    assert models != null && models.length >= 1;
                    if(!TaskQueue.inQueue(models[0].getAbsolutePath())
                            && user_dir.lastModified() < oldest) {
                        oldest = user_dir.lastModified();
                        delete = user_dir;
                    }
                }
                if(delete != null) {
                    delete.delete();
                }else{
                    return new UploadModelResponse(
                            "Queue size exceeds maximum. Please upload the model later.",
                            enums.UploadAuthState.DENIED
                    );
                }
            }
        }
        // step 3: if exists, check is in task queue(using dir). if in, need cancel
        else {
            File[] models = file.listFiles();
            assert models != null && models.length >= 1;
            File cur = models[0];
            if(TaskQueue.inQueue(cur.getAbsolutePath())) {
                return new UploadModelResponse(
                        "Need Cancel Training of " + cur.getName(),
                        enums.UploadAuthState.NEEDS_CANCEL
                );
            }
            // if not in queue, check if same model, if not, delete cache
            if(!cur.getName().equals(dir_name)) {
                cur.delete();
            }
        }
        return new UploadModelResponse("Allowed to upload.", enums.UploadAuthState.ALLOWED);
    }
}
