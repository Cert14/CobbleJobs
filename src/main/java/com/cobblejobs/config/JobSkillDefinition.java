package com.cobblejobs.config;

import java.util.Map;

public class JobSkillDefinition {
    private String id;
    private SkillType type;
    private int unlockLevel;
    private Map<String, Object> data;

    public String getId() {
        return id;
    }

    public SkillType getType() {
        return type;
    }

    public int getUnlockLevel() {
        return unlockLevel;
    }

    public Map<String, Object> getData() {
        return data;
    }
}

