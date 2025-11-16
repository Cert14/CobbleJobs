package com.cobblejobs.player;

import java.util.HashMap;
import java.util.Map;

public class PlayerJobData {

    private Map<String, JobProgress> jobs = new HashMap<>();
    private double balance;

    public Map<String, JobProgress> getJobs() {
        return jobs;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public JobProgress getOrCreateProgress(String jobId) {
        return jobs.computeIfAbsent(jobId, JobProgress::new);
    }
}

