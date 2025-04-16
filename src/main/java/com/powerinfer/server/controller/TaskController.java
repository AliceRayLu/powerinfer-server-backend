package com.powerinfer.server.controller;

import com.powerinfer.server.entity.Key;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    @Autowired
    private KeyService keyService;

    private Task getTask(String uid, String mname, String tname) {
        Model model = modelService.getModel(mname, uid);
        if (model == null) { return null; }
        Type type = typeService.getTypeByMidAndName(model.getMid(), tname);
        if (type == null) { return null; }
        return taskService.findLatestTaskByTid(type.getTid());
    }


//    @PostMapping(value = "/query", produces = MediaType.APPLICATION_NDJSON_VALUE)
//    public Flux<Task> query(@RequestAttribute("uid") String uid, @RequestParam String mname, @RequestParam String tname){
//        // all the training record of a single model type
//        Model model = modelService.getModel(mname, uid);
//        assert model != null;
//        Type type = typeService.getTypeByMidAndName(model.getMid(), tname);
//        assert type != null;
//        return taskService.findByTid(type.getTid());
//    }

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
    public ResponseEntity<String> initialTask(@RequestParam String mname,
                                              @RequestAttribute("uid") String uid,
                                              @RequestParam String tname,
                                              @RequestParam boolean need_train,
                                              @RequestParam(required = false) String hf_path) {
        String dir_name = mname + "-" + tname;
        UploadModelResponse response = taskService.checkAuth(uid, dir_name);
        if(response.getAuthState() != enums.UploadAuthState.ALLOWED){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response.getMsg());
        }
        String dir = AddreessManager.getUploadedPath(uid, dir_name, need_train);
        String output_dir = AddreessManager.getUploadedPath(uid, dir_name, false);
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
        Task t = taskService.addTask(type.getTid(), dir, version, need_train, output_dir);
        if (hf_path != null && !hf_path.isEmpty()) {
            System.err.println("Entering dealing with hf");
            List<Key> tokens = keyService.getHfListOfUser(uid);
            if(tokens == null || tokens.isEmpty()){
                response = new UploadModelResponse("No huggingface token added to the account.",
                        enums.UploadAuthState.NO_HF_TOKEN);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response.getMsg());
            }
            CompletableFuture.runAsync(() -> {
                System.err.println("Entering async function running.");
                boolean success = false;
                for (Key token : tokens) {
                    String loginCMD = String.format("huggingface-cli login --token %s", token.getContent());
                    try {
                        Process loginProcess = Runtime.getRuntime().exec(loginCMD);
                        int loginExitCode = loginProcess.waitFor();
                        if (loginExitCode == 0){
                            System.err.println("Huggingface login successful.");
                            String downloadCMD = String.format(
                                   "huggingface-cli download %s --resume-download --local-dir %s",
                                    hf_path, dir);
                            Process downloadProcess = Runtime.getRuntime().exec(downloadCMD);
                            System.err.println("Starting Huggingface download.");
                            // print download process
                            try (BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(downloadProcess.getInputStream()))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    System.err.println("[HuggingFace Download] " + line);
                                }
                            }
                            
                            int downloadExitCode = downloadProcess.waitFor();
                            if (downloadExitCode == 0){
                                System.err.println("Huggingface download successful.");
                                success = true;
                                break;
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("HuggingFace io exception: " + e.getMessage());
                    } catch (InterruptedException e) {
                        System.err.println("HuggingFace download interrupted: " + e.getMessage());
                    } catch (Exception e) {
                        System.err.println("HuggingFace unknown exception: " + e.getMessage());
                    }
                }
                if(success){
                    t.setState(enums.TaskState.QUEUED);
                    taskService.updateById(t);
                }else{
                    taskService.removeById(t);
                }
                taskService.updateTask(t, !success);
            });
        }
        return ResponseEntity.ok(version);
    }

    @PostMapping("/client/done")
    public void startTask(@RequestAttribute("uid") String uid,
                          @RequestParam String mname,
                          @RequestParam String tname,
                          @RequestParam boolean success) {
        // local models successfully uploaded, start training task
        Task task = getTask(uid, mname, tname);
        assert task != null;
        System.err.println("[log info] task id: " + task.getId() + " task outputDir: " + task.getOutputDir());
        if (success) {
            System.err.println("[log info] Moving into task queues" );
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
        String remotePath = AddreessManager.getUploadedPath(uid, name, request.isNeed_train());
        File file = new File(remotePath);
        if (file.exists()) {
            String old = JsonOperator.getMd5(
                    AddreessManager.getUploadedPath(uid, dir_name, request.isNeed_train()),
                    request.getFname());
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
        JsonOperator.writeMd5(
                AddreessManager.getUploadedPath(uid, dir_name, request.isNeed_train()),
                request.getFname(), request.getMd5());
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(path= "/client/upload", method = RequestMethod.PATCH)
    public ResponseEntity<?> uploadChunk(
            @RequestAttribute("uid") String uid,
            @RequestHeader("Content-Range") String contentRange,
            @RequestParam String mname,
            @RequestParam String tname,
            @RequestParam String fname,
            @RequestParam boolean need_train,
            @RequestBody byte[] chunk) throws IOException {
        String name = mname + "-" + tname + AddreessManager.getSeperator() + fname;
        String[] range = contentRange.split(" ")[1].split("/");
        String[] byteRange = range[0].split("-");
        long startByte = Long.parseLong(byteRange[0]);
        long totalSize = Long.parseLong(range[1]);

        // create files
        File file = new File(AddreessManager.getUploadedPath(uid, name, need_train));
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
    public ResponseEntity<Boolean> cancelTask(
            @RequestAttribute("uid") String uid,
            @RequestParam String mname,
            @RequestParam String tname
    ) {
        Task task = getTask(uid, mname, tname);
        if (task == null || task.getState() == enums.TaskState.FAILED || task.getState() == enums.TaskState.SUCCESS) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskService.cancelTask(task.getTid()));
    }
}
