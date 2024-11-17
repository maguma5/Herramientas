package com.herramientas;

public class Worker {
    private String workerName;
    private String time;
    private String machine;
    private String status;

    public Worker(String workerName, String time, String machine, String status) {
        this.workerName = workerName;
        this.time = time;
        this.machine = machine;
        this.status = status;
    }
    public String getWorkerName() {
        return workerName;
    }
    public String getTime() {
        return time;
    }
    public String getMachine() {
        return machine;
    }
    public String getStatus() {
        return status;
    }
}