package com.powerinfer.server.controller;

import com.powerinfer.server.entity.Model;
import com.powerinfer.server.entity.Type;
import com.powerinfer.server.entity.User;
import com.powerinfer.server.requestParams.GetModelRequest;
import com.powerinfer.server.responseParams.GetModelResponse;
import com.powerinfer.server.service.*;
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

    private GetModelResponse getModelRequest(String uid, GetModelRequest request) {
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

    @PostMapping(value = "/client/get", produces = "application/json")
    public GetModelResponse getModelType(@RequestAttribute("uid") String uid, @RequestBody GetModelRequest request){
        GetModelResponse response = getModelRequest(uid, request);
        if (response.getState() == enums.GetModelState.SUCCESS) {
            Type type = response.getModelType();
            Model model = modelService.getById(type.getMid());
            model.addDown();
            modelService.updateById(model);
            return response;
        }
        return response;
    }

    @PostMapping("/single/get")
    public GetModelResponse getModelTypeWeb(@RequestParam String uid, @RequestBody GetModelRequest request){
        return getModelRequest(uid, request);
    }

    @PostMapping("/get/file")
    public ResponseEntity<String> getTypeList(@RequestParam String path) throws IOException {
        Path filePath = Paths.get(path);
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        long fileSize = resource.contentLength();
        if (fileSize > 50 * 1024 * 1024) {  // file over 50MB will not be sent
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(null);
        }

        String content = new String(resource.getInputStream().readAllBytes());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/plain")
                .body(content);
    }

    @PostMapping({"/remove"})
    public void remove(@RequestBody Type type) {
        taskService.removeTasksByTid(type.getTid());
        Model model = modelService.getById(type.getMid());
        model.removeType(type.getName());
        modelService.updateById(model);
        typeService.removeById(type.getTid());
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
