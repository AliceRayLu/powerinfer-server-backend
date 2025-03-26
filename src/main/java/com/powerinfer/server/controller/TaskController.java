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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

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


    @PostMapping("/client/query")
    public List<Task> query(@RequestAttribute("uid") String uid, @RequestParam String mname, @RequestParam String tname){
        // TODO
        return null;
    }

    @PostMapping("/client/add")
    public void initialTask(@RequestParam String mname, @RequestAttribute("uid") String uid,@RequestParam String tname) {
        String dir = AddreessManager.getUploadedPath(uid, mname+"-"+tname);
        Model model = modelService.getModel(mname, uid);
        if (model == null) {
            modelService.save(new Model(mname, uid, enums.Visibility.PUBLIC));
        }
        model = modelService.getModel(mname, uid);
        Type type = typeService.getTypeByMidAndName(model.getMid(), uid);
        String version = JsonOperator.generateVersion(model.getMid());
        if (type == null) {
            typeService.save(new Type(tname, model.getMid(), dir, version));
        }
        type = typeService.getTypeByMidAndName(model.getMid(), uid);
        taskService.addTask(type.getTid(), dir, version);
    }

    @RequestMapping(path = "/client/upload", method = RequestMethod.HEAD)
    public ResponseEntity<?> getUploadStatus(@RequestAttribute("uid") String uid,
                                             @RequestBody UploadModelRequest request) throws IOException {
        String dir_name = request.getMname() + "-" + request.getTname();
        UploadModelResponse response = taskService.checkAuth(uid, dir_name);
        if(response.getAuthState() != enums.UploadAuthState.ALLOWED){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response.getMsg());
        }
        String name = dir_name + "/" + request.getFname();
        String remotePath = AddreessManager.getUploadedPath(uid, name);
        File file = new File(remotePath);
        if (file.exists()) {
            String old = JsonOperator.getMd5(dir_name, request.getFname());
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
        JsonOperator.writeMd5(dir_name, request.getFname(), request.getMd5());
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
        String name = mname + "-" + tname + "/" + fname;
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
}
