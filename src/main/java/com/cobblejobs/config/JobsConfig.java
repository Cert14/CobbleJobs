package com.cobblejobs.config;

import java.util.List;

public class JobsConfig {
    private int maxJobsPerPlayer;
    private List<JobDefinition> jobs;

    public int getMaxJobsPerPlayer() {
        return maxJobsPerPlayer;
    }

    public List<JobDefinition> getJobs() {
        return jobs;
    }
}

