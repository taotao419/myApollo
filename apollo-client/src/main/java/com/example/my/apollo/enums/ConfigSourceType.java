package com.example.my.apollo.enums;

/**
 * ConfigSourceType
 */
public enum ConfigSourceType {
    REMOTE("Loaded from remote config service"), LOCAL("Loaded from local cache"), NONE("Load failed");

    private final String description;

    ConfigSourceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}