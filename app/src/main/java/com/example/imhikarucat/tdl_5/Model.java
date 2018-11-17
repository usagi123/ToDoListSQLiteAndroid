package com.example.imhikarucat.tdl_5;

public class Model {
    private int id;
    private String task;
    private String duration;
    private String status;

    public Model(int id, String task, String duration, String status) {
        this.id = id;
        this.task = task;
        this.duration = duration;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
