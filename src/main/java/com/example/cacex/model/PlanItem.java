package com.example.cacex.model;

public class PlanItem {

    private Action action;
    private FileCategory fileCategory;
    private String scope;
    private String key;
    private String sourcePath;
    private Object payload;

    public PlanItem() {
    }

    public PlanItem(Action action, FileCategory fileCategory, String scope, String key, String sourcePath, Object payload) {
        this.action = action;
        this.fileCategory = fileCategory;
        this.scope = scope;
        this.key = key;
        this.sourcePath = sourcePath;
        this.payload = payload;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public FileCategory getFileCategory() {
        return fileCategory;
    }

    public void setFileCategory(FileCategory fileCategory) {
        this.fileCategory = fileCategory;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public String getScope() {
        return scope;
    }


}
