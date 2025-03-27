package com.powerinfer.server.utils;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddreessManager {
    private static final String store_folder = "D://store";
    private static final String train_folder = "D://train";

    // FIXME: script path
    private static final String train_python_script = "D:\\projects\\LLM\\server\\src\\main\\resources\\train.py";
    private static final String verify_python_script = "D:\\projects\\LLM\\server\\src\\main\\resources\\verify.py";

    public static String getTrainDir() { return train_folder; }

    public static String getUploadedPath(String uid, String name) {
        return train_folder + "/" + uid + "/" + name;
    }
    public static String getTrainPythonPath() { return train_python_script; }
    public static String getVerifyPythonPath() { return verify_python_script; }

    public static void deleteDir(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteDir(f);
                }
            }
        }
        dir.delete();
    }
}
