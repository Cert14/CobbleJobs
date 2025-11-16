package com.cobblejobs.player;

public class JobProgress {
    private String jobId;
    private int level;
    private double xp;

    public JobProgress() {
    }

    public JobProgress(String jobId) {
        this.jobId = jobId;
        this.level = 1;
        this.xp = 0.0;
    }

    public String getJobId() {
        return jobId;
    }

    public int getLevel() {
        return level;
    }

    public double getXp() {
        return xp;
    }

    public void addXp(double amount, int maxLevel) {
        if (level >= maxLevel) {
            return;
        }
        this.xp += amount;
        // Simple linear curve: level up every 100 XP
        while (level < maxLevel && xp >= 100.0) {
            xp -= 100.0;
            level++;
        }
    }
}

