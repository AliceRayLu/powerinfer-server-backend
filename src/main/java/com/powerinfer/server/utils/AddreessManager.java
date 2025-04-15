package com.powerinfer.server.utils;


import java.io.File;
import java.nio.file.Paths;

public class AddreessManager {
    // FIXME: script path
    private static final String baseDir = "/mnt/lbh/.powerinfer/";
    private static final String store_folder = baseDir + "store/";
    private static final String train_folder = baseDir + "train/";
    private static final String train_python_script = baseDir + "train.py";
    private static final String verify_python_script = baseDir + "verify.py";
    private static final String seperator = File.separator;

    private static String normalize(String path) {
        return Paths.get(path).normalize().toString();
    }
    public static String getTrainDir() { return normalize(train_folder); }
    public static String getSeperator() { return seperator; }
    public static String getUploadedPath(String uid, String name, boolean need_train) {
        if (need_train) {
            return normalize(train_folder + seperator + uid + seperator + name);
        }
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
