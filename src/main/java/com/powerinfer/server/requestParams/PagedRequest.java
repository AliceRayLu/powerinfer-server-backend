package com.powerinfer.server.requestParams;

public class PagedRequest {
    private int page;
    private int size;
    private String sortBy;
    private String search;
    private String user;

    public int getPage() {return page;}
    public int getSize() {return size;}
    public String getSortBy() {return sortBy;}
    public String getSearch() {return search;}
    public String getUser() {return user;}
}
