package com.cobblejobs.config;

import java.util.Map;

public class JobActionDefinition {
    private String id;
    private JobEventType event;
    private double baseMoney;
    private double baseXp;
    private Map<String, Object> filters;

    public String getId() {
        return id;
    }

    public JobEventType getEvent() {
        return event;
    }

    public double getBaseMoney() {
        return baseMoney;
    }

    public double getBaseXp() {
        return baseXp;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }
}

