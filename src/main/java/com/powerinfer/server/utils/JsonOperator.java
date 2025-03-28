package com.powerinfer.server.utils;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public class JsonOperator {
    private static final Gson gson = new Gson();

    public static String getJsonString(Map<String, Object> structure) {
        return gson.toJson(structure);
    }

    public static String generateVersion(String id){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            String input = id + System.currentTimeMillis();
            byte[] encodedHash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte hash : encodedHash) {
                String hex = Integer.toHexString(0xff & hash);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException");
        }
        return null;
    }

    public static void writeMd5(String dir_name, String file_name, String md5) throws IOException {
        File file = new File(dir_name + AddreessManager.getSeperator()+".md5");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        List<String> content = Files.readAllLines(file.toPath());
        boolean changed = false;
        for (int i = 0; i < content.size(); i++) {
            String[] info = content.get(i).split(":");
            if(info[0].equals(file_name)){
                changed = true;
                content.set(i, info[0] + ":" + md5);
            }
        }
        if (!changed) {
            content.add(file_name+":"+md5);
        }
        Files.write(file.toPath(), content);
    }

    public static String getMd5(String dir_name, String file_name) throws IOException {
        File file = new File(dir_name + AddreessManager.getSeperator()+ ".md5");
        if (!file.exists()) {return null;}
        List<String> content = Files.readAllLines(file.toPath());
        for (String line : content) {
            String[] info = line.split(":");
            if(info[0].equals(file_name)){
                return info[1];
            }
        }
        return null;
    }
}
