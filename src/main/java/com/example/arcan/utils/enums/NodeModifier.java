package com.example.arcan.utils.enums;

public enum  NodeModifier {
    PACKAGE("PACKAGE"), CLASS("CLASS"), ABSTRACT("ABSTRACT"), INTERFACE("INTERFACE");

    private String value;

    NodeModifier(String value) {
        this.value = value;
    }

    public String toString(){
        return value;
    }
}
