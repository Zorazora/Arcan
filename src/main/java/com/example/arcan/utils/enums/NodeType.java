package com.example.arcan.utils.enums;

public enum NodeType {
    INTERNAL("INTERNAL"), EXTERNAL("EXTERNAL");

    private String value;

    NodeType(String value) {
        this.value = value;
    }

    public String toString(){
        return value;
    }
}
