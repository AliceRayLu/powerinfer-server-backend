package com.powerinfer.server.utils;


import java.io.File;
import java.nio.file.Paths;

public class AddreessManager {
    // FIXME: script path
    private static final String pythonPath = "/mnt/miniconda3/bin/python";
    private static final String baseDir = "/mnt/lbh/.powerinfer/";
    private static final String store_folder = baseDir + "store/";
    private static final String train_folder = baseDir + "train/";
    private static final String verify_python_script = baseDir + "verify.py";
    private static final String seperator = File.separator;

    private static final String write_yaml_script = baseDir + "write_yaml.py";
    private static final String train_yaml = "/mnt/lbh/LLama-Factory/examples/train_lora/llama3_lora_sft.yaml";
    private static final String merge_yaml = "/mnt/lbh/LLama-Factory/examples/merge_lora/llama3_lora_sft.yaml";

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

    public static String getWrite_yaml_script() { return normalize(write_yaml_script); }
    public static String getTrain_yaml() { return normalize(train_yaml); }
    public static String getMerge_path() { return normalize(merge_yaml); }

    public static String getVerifyPythonPath() { return normalize(verify_python_script); }
    public static String getPythonPath() { return normalize(pythonPath); }

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
