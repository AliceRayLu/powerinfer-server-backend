package com.powerinfer.server.utils;

import com.google.gson.Gson;

import java.util.Map;

public class JsonOperator {
    private static final Gson gson = new Gson();

    public static String getJsonString(Map<String, Object> structure) {
        return gson.toJson(structure);
    }
}
