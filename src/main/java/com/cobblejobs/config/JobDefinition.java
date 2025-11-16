package com.cobblejobs.config;

import java.util.List;

public class JobDefinition {
    private String id;
    private String displayName;
    private String origin;
    private JobCategory category;
    private int maxLevel;
    private List<JobActionDefinition> actions;
    private List<JobSkillDefinition> skills;

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getOrigin() {
        return origin;
    }

    public JobCategory getCategory() {
        return category;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public List<JobActionDefinition> getActions() {
        return actions;
    }

    public List<JobSkillDefinition> getSkills() {
        return skills;
    }
}

