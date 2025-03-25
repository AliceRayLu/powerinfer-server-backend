package com.powerinfer.server.utils;


public class AddreessManager {
    private static final String uploaded_folder = "D://uploaded";

    public String getUploadedPath(String uid, String name) {
        return uploaded_folder + "/" + uid + "/" + name;
    }

}
