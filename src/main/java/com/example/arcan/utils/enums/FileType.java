package com.example.arcan.utils.enums;

public enum FileType {
    PACKAGE("PACKAGE"), CLASS("CLASS");

    private String value;

    FileType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
