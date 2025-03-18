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
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/client/get")
    public GetModelResponse getModelType(@RequestAttribute("uid") String uid, @RequestBody GetModelRequest request){
        User user = userService.getUserByUsername(request.getUname());
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

    @PostMapping("/client/update")
    public void updateType(String mname, String uid, String tname, String file){
        Model model = modelService.getModel(mname, uid);
        if(model == null){
            model = new Model(mname, uid, Visibility.PUBLIC);
            modelService.save(model);
        }

    }
}
