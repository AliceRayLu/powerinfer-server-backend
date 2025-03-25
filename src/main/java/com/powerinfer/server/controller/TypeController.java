package com.powerinfer.server.controller;

import com.powerinfer.server.entity.Model;
import com.powerinfer.server.entity.Type;
import com.powerinfer.server.entity.User;
import com.powerinfer.server.requestParams.GetModelRequest;
import com.powerinfer.server.responseParams.GetModelResponse;
import com.powerinfer.server.service.ModelService;
import com.powerinfer.server.service.TaskService;
import com.powerinfer.server.service.TypeService;
import com.powerinfer.server.service.UserService;
import com.powerinfer.server.utils.AddreessManager;
import com.powerinfer.server.utils.PartialResource;
import com.powerinfer.server.utils.enums;
import com.powerinfer.server.utils.enums.Visibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/type")
public class TypeController {
    @Autowired
    private ModelService modelService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private UserService userService;
    @Autowired
    private TaskService taskService;

    @PostMapping("/client/add")
    public void add(@RequestAttribute("uid") String uid, @RequestParam String name){
        Model model = modelService.getModel(name.split(":")[0], uid);
        Type type = typeService.getTypeByMidAndName(model.getMid(), name.split(":")[1]);
        taskService.addTask(type.getTid(),type.getDir());
    }

    @PostMapping("/client/query")
    public int query(@RequestAttribute("uid") String uid, @RequestParam String name){
        Model model = modelService.getModel(name.split(":")[0], uid);
        Type type = typeService.getTypeByMidAndName(model.getMid(), name.split(":")[1]);
        return taskService.checkPos(type.getTid());
    }


    @PostMapping(value = "/client/get", produces = "application/json")
    public GetModelResponse getModelType(@RequestAttribute("uid") String uid, @RequestBody GetModelRequest request){
        String uname = request.getUname();
        User user = uname==null ? userService.getById(uid) : userService.getUserByUsername(uname);
        GetModelResponse response = new GetModelResponse(enums.GetModelState.SUCCESS);
        if (user == null) {
            response.setState(enums.GetModelState.MODEL_NOT_FOUND);
            response.setMessage("User "+request.getUname()+" not found.");
            return response;
        }
        Model model = modelService.getModel(request.getMname(), user.getUid());
        if (model == null) {
            response.setState(enums.GetModelState.MODEL_NOT_FOUND);
            response.setMessage("Model "+request.getMname()+" not found.");
            return response;
        }
        if(!user.getUid().equals(uid) && model.getVisibility() == Visibility.PRIVATE) {
            response.setState(enums.GetModelState.MODEL_UNACCESSIBLE);
            response.setMessage("Model "+request.getMname()+" is private.");
            return response;
        }
        Type type = typeService.getTypeByMidAndName(model.getMid(), request.getTname());
        if (type == null) {
            response.setState(enums.GetModelState.TYPE_NOT_FOUND);
            response.setMessage("Type "+request.getTname()+" not found.");
            return response;
        }
        response.setModelType(type);
        return response;
    }

    @PostMapping("/get")
    public List<Type> getTypeList(@RequestParam String mid){
        return typeService.getAllTypes(mid);
    }

    @PostMapping("/get/detail")
    public Type getTypeDetail(String tid){
        return typeService.getById(tid);
    }

    @PostMapping({"/remove", "/client/remove"})
    public void remove(){

    }

//    @PostMapping("/client/update")
    public void updateType(String mname, String uid, String tname){
        String file_path = AddreessManager.getUploadedPath(uid, mname+"-"+tname);
        System.out.println("updating with file path  = "+file_path);
        Model model = modelService.getModel(mname, uid);
        if(model == null){
            model = new Model(mname, uid, Visibility.PUBLIC);
            modelService.save(model);
        }else{
            System.out.println("model downloaded numbers: "+ model.getNumDown());
            model.update();
            modelService.updateById(model);
        }
        model = modelService.getModel(mname, uid);
        Type type = typeService.getTypeByMidAndName(model.getMid(), tname);
        if(type == null){
            type = new Type(
                    tname,
                    model.getMid(),
                    file_path
            );
            typeService.save(type);
        }else{
            type.updateVersion();
            type.updateDir(file_path);
            typeService.updateById(type);
        }
    }

    @RequestMapping(path = "/client/upload", method = RequestMethod.HEAD)
    public ResponseEntity<?> getUploadStatus(@RequestAttribute("uid") String uid,
                                             @RequestParam String mname,
                                             @RequestParam String tname,
                                             @RequestParam String fname) throws IOException {
        String name = mname + "-" + tname + "/" + fname;
        String remotePath = AddreessManager.getUploadedPath(uid, name);
        File file = new File(remotePath);
        if (file.exists()) {
            long fileSize = file.length();
            return ResponseEntity.ok()
                    .header("Content-Range", "bytes 0-" + (fileSize - 1) + "/" + fileSize)
                    .build();
        }
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
            updateType(mname, uid, tname);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).build();
        }
    }

    @PostMapping("/download")
    public ResponseEntity<Resource> downloadLargeFile(@RequestHeader(value = "Range", required = false) String rangeHeader, @RequestParam String path) throws IOException {
        Path filePath = Paths.get(path);
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        long fileSize = resource.contentLength();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring(6).split("-");
            long start = Long.parseLong(ranges[0]);
            long end = ranges.length > 1 ? Long.parseLong(ranges[1]) : fileSize - 1;

            headers.add(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize);

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .body(new PartialResource(resource, start, end));
        } else {
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize));
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        }
    }
}
