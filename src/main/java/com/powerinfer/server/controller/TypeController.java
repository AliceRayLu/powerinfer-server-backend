package com.powerinfer.server.controller;

import com.powerinfer.server.entity.Model;
import com.powerinfer.server.entity.Type;
import com.powerinfer.server.entity.User;
import com.powerinfer.server.requestParams.GetModelRequest;
import com.powerinfer.server.responseParams.GetModelResponse;
import com.powerinfer.server.service.ModelService;
import com.powerinfer.server.service.TypeService;
import com.powerinfer.server.service.UserService;
import com.powerinfer.server.utils.enums;
import com.powerinfer.server.utils.enums.Visibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
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

    private static final String UPLOAD_DIR = "D://uploaded//";

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
        System.out.println("REACHING here====================>");
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

    @PostMapping("/client/update")
    public void updateType(String mname, String uid, String tname, String file_path){
        Model model = modelService.getModel(mname, uid);
        if(model == null){
            model = new Model(mname, uid, Visibility.PUBLIC);
            modelService.save(model);
        }

    }

    @RequestMapping(path = "/client/upload", method = RequestMethod.HEAD)
    public ResponseEntity<?> getUploadStatus(@RequestParam String name) throws IOException {
        File file = new File( UPLOAD_DIR + name);
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
            @RequestHeader("Content-Range") String contentRange,
            @RequestParam String name,
            @RequestBody byte[] chunk) throws IOException {

        String[] range = contentRange.split(" ")[1].split("/");
        String[] byteRange = range[0].split("-");
        long startByte = Long.parseLong(byteRange[0]);
        long totalSize = Long.parseLong(range[1]);

        Files.createDirectories(Paths.get(UPLOAD_DIR));

        try (FileOutputStream fos = new FileOutputStream(UPLOAD_DIR + name, true)) {
            fos.write(chunk);
        }

        if (startByte + chunk.length >= totalSize) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).build();
        }
    }
}

/**
 * The address manager:
 *
 *
 */