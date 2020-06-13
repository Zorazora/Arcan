package com.example.arcan.utils;

import lombok.Data;
import org.apache.bcel.classfile.JavaClass;

@Data
public class ClassNode {
    private String name;
    private JavaClass content;
    private String parent;

    public ClassNode(String name, JavaClass content, String parent) {
        this.name = name;
        this.content = content;
        this.parent = parent;
    }
}
