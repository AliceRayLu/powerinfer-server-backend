package com.powerinfer.server.entity;

import com.powerinfer.server.utils;

import java.util.Date;

public class Model {
    private String mid;
    private utils.Visibility visibility;
    private int num_down; // number of download times
    private Date date; // last updated time
    private String uid;
    private utils.Arch arch;
    // TODO: how to store readme

}
