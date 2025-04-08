package com.powerinfer.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.powerinfer.server.utils.JsonOperator.getJsonString;

@TableName("types")
public class Type {
    @TableId(type = IdType.ASSIGN_UUID)
    private String tid;
    @JsonProperty("name")
    private String name;
    @JsonProperty("version")
    private String version;
    @JsonProperty("mid")
    private String mid;
    @JsonProperty("size")
    private long size;

    @JsonProperty("dir")
    private String dir; // directory of model files in server
    @JsonProperty("dir_info")
    @TableField("dirInfo")
    private String dirInfo; // serialize as json format
    @JsonProperty("size_info")
    @TableField("sizeInfo")
    private String sizeInfo; // record the size of a file



    public Type() {}
    public Type(String name, String mid, String dir, String version) {
        this.name = name;
        this.mid = mid;
        this.dir = dir;
        this.version = version;
        this.dirInfo = String.valueOf(getJsonString(getStructure(dir)));
        this.sizeInfo = String.valueOf(getJsonString(getFileSize(dir)));
    }

    public void updateVersion(String version) {
        this.version = version;
    }

    public void updateDir(String dir) {
        this.dir = dir;
        this.dirInfo = String.valueOf(getJsonString(getStructure(dir)));
        this.sizeInfo = String.valueOf(getJsonString(getFileSize(dir)));
    }


    private Map<String, Object> getStructure(String path) {
        File folder = new File(path);
        Map<String, Object> structure = new HashMap<>();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith(".")) continue; // ignore all config files
                if (file.isDirectory()) {
                    structure.put(file.getName(), getStructure(file.getPath()));
                } else {
                    structure.put(file.getName(), file.getAbsolutePath());
                }
            }

        }
        return structure;
    }

    private Map<String, Object> getFileSize(String path) {
        size = 0;
        File folder = new File(path);
        Map<String, Object> structure = new HashMap<>();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith(".")) continue;
                if (file.isDirectory()) {
                    structure.put(file.getName(), getFileSize(file.getPath()));
                } else {
                    structure.put(file.getName(), file.length());
                    size += file.length();
                }
            }
        }
        return structure;
    }

    public String getDirInfo(){return dirInfo;}
    public String getSizeInfo(){return sizeInfo;}
    public void setDirInfo(String dirInfo){this.dirInfo = dirInfo;}
    public void setSizeInfo(String sizeInfo){this.sizeInfo = sizeInfo;}
    public String getTid(){return tid;}
    public String getDir(){return dir;}
    public String getMid(){return mid;}
    public String getVersion(){return version;}
    public String getName(){return name;}
}
