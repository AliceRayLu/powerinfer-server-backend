package com.powerinfer.server.controller;

import com.powerinfer.server.entity.Model;
import com.powerinfer.server.entity.Task;
import com.powerinfer.server.entity.Type;
import com.powerinfer.server.requestParams.UploadModelRequest;
import com.powerinfer.server.responseParams.UploadModelResponse;
import com.powerinfer.server.service.*;
import com.powerinfer.server.utils.AddreessManager;
import com.powerinfer.server.utils.JsonOperator;
import com.powerinfer.server.utils.enums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private ModelService modelService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private TaskService taskService;

    private Task getTask(String uid, String mname, String tname) {
        Model model = modelService.getModel(mname, uid);
        if (model == null) { return null; }
        Type type = typeService.getTypeByMidAndName(model.getMid(), tname);
        if (type == null) { return null; }
        return taskService.findLatestTaskByTid(type.getTid());
    }


    @PostMapping(value = "/query", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Task> query(@RequestAttribute("uid") String uid, @RequestParam String mname, @RequestParam String tname){
        Model model = modelService.getModel(mname, uid);
        assert model != null;
        Type type = typeService.getTypeByMidAndName(model.getMid(), tname);
        assert type != null;
        return taskService.findByTid(type.getTid());
    }

    @PostMapping("/client/detail")
    public ResponseEntity<Task> queryDetail(@RequestAttribute("uid") String uid,
                            @RequestParam String mname,
                            @RequestParam String tname){
        Task task = getTask(uid, mname, tname);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        if (task.getState() == enums.TaskState.QUEUED) {
            int pos = taskService.checkPos(task.getTid());
            task.setQueue(pos);
            task.setWaitingTime(taskService.getWaitingMinutes(pos));
        }
        if (task.getState() == enums.TaskState.RUNNING) {
            // TODO: get progress and left time
        }
        return ResponseEntity.ok(task);
    }

    @PostMapping("/client/add")
    public ResponseEntity<String> initialTask(@RequestParam String mname, @RequestAttribute("uid") String uid,@RequestParam String tname) {
        String dir_name = mname + "-" + tname;
        UploadModelResponse response = taskService.checkAuth(uid, dir_name);
        if(response.getAuthState() != enums.UploadAuthState.ALLOWED){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response.getMsg());
        }
        String dir = AddreessManager.getUploadedPath(uid, dir_name);
        Model model = modelService.getModel(mname, uid);
        if (model == null) {
            modelService.save(new Model(mname, uid, enums.Visibility.PUBLIC));
        }
        model = modelService.getModel(mname, uid);
        Type type = typeService.getTypeByMidAndName(model.getMid(), tname);
        String version = JsonOperator.generateVersion(model.getMid());
        if (type == null) {
            typeService.save(new Type(tname, model.getMid(), dir, version));
        }
        type = typeService.getTypeByMidAndName(model.getMid(), tname);
        taskService.addTask(type.getTid(), dir, version);
        return ResponseEntity.ok(version);
    }

    @PostMapping("/client/done")
    public void startTask(@RequestAttribute("uid") String uid,
                          @RequestParam String mname,
                          @RequestParam String tname,
                          @RequestParam boolean success) {
        Task task = getTask(uid, mname, tname);
        assert task != null;
        if (success) {
            task.setState(enums.TaskState.QUEUED);
            taskService.updateById(task);
        }else {
            taskService.removeById(task);
        }
        taskService.updateTask(task, !success);
    }

    @RequestMapping(path = "/client/upload", method = RequestMethod.HEAD)
    public ResponseEntity<?> getUploadStatus(@RequestAttribute("uid") String uid,
                                             @RequestBody UploadModelRequest request) throws IOException {
        String dir_name = request.getMname() + "-" + request.getTname();
        String name = dir_name + AddreessManager.getSeperator() + request.getFname();
        String remotePath = AddreessManager.getUploadedPath(uid, name);
        File file = new File(remotePath);
        if (file.exists()) {
            String old = JsonOperator.getMd5(AddreessManager.getUploadedPath(uid, dir_name), request.getFname());
            assert old != null;
            if (!old.equals(request.getMd5())) {
                file.delete();
            }else {
                long fileSize = file.length();
                return ResponseEntity.ok()
                        .header("Content-Range", "bytes 0-" + (fileSize - 1) + "/" + fileSize)
                        .build();
            }
        }
        JsonOperator.writeMd5(AddreessManager.getUploadedPath(uid, dir_name), request.getFname(), request.getMd5());
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(path= "/client/upload", method = RequestMethod.PATCH)
    public ResponseEntity<?> uploadChunk(
            @RequestAttribute("uid") String uid,
            @RequestHeader("Content-Range") String contentRange,
            @RequestParam String mname,
            @RequestParam String tname,
            @RequestParam String fname,
            @RequestBody byte[] chunk) throws IOException {
        String name = mname + "-" + tname + AddreessManager.getSeperator() + fname;
        String[] range = contentRange.split(" ")[1].split("/");
        String[] byteRange = range[0].split("-");
        long startByte = Long.parseLong(byteRange[0]);
        long totalSize = Long.parseLong(range[1]);

        // create files
        File file = new File(AddreessManager.getUploadedPath(uid, name));
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            fos.write(chunk);
        }

        if (startByte + chunk.length >= totalSize || startByte + chunk.length == totalSize - 1) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).build();
        }
    }

    @PostMapping("/client/cancel")
    public ResponseEntity<Boolean> cancelTask(@RequestAttribute("uid") String uid, @RequestParam String mname, @RequestParam String tname) {
        Task task = getTask(uid, mname, tname);
        if (task == null) { return ResponseEntity.notFound().build(); }
        return ResponseEntity.ok(taskService.cancelTask(task.getTid()));
    }
}
