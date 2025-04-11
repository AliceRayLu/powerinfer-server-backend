package com.powerinfer.server.utils;


import java.io.File;
import java.nio.file.Paths;

public class AddreessManager {
    // FIXME: script path
    private static final String store_folder = "D://store";
    private static final String train_folder = "D://train";
    private static final String train_python_script = "D:\\projects\\LLM\\server\\src\\main\\resources\\train.py";
    private static final String verify_python_script = "D:\\projects\\LLM\\server\\src\\main\\resources\\verify.py";
    private static final String seperator = File.separator;

    private static String normalize(String path) {
        return Paths.get(path).normalize().toString();
    }
    public static String getTrainDir() { return normalize(train_folder); }
    public static String getSeperator() { return seperator; }
    public static String getUploadedPath(String uid, String name) {
        return normalize(train_folder + seperator + uid + seperator + name);
    }
    public static String getStorePath(String uid, String name) {
        return normalize(store_folder + seperator + uid + seperator + name);
    }

    public static String getTrainPythonPath() { return normalize(train_python_script); }
    public static String getVerifyPythonPath() { return normalize(verify_python_script); }

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
