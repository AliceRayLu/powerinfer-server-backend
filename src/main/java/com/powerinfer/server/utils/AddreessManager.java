package com.powerinfer.server.utils;


import java.util.ArrayList;
import java.util.List;

public class AddreessManager {
    private static final String uploaded_folder = "D://uploaded";
    private static final String train_folder = "D://train";

    // FIXME: script path
    private static final String train_python_script = "D:\\projects\\LLM\\server\\src\\main\\resources\\train.py";
    private static final String verify_python_script = "D:\\projects\\LLM\\server\\src\\main\\resources\\verify.py";


    public static String getUploadedPath(String uid, String name) {
        return uploaded_folder + "/" + uid + "/" + name;
    }
    public static String getTrainPythonPath() { return train_python_script; }
    public static String getVerifyPythonPath() { return verify_python_script; }


}
