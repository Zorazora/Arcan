package com.example.arcan.utils;

import com.example.arcan.utils.enums.FileType;
import lombok.Data;
import org.apache.bcel.classfile.JavaClass;

import java.util.ArrayList;

@Data
public class FileNode {
    private String name;
    private FileType type;
    private JavaClass content;
    private ArrayList<FileNode> children;

    public FileNode(String name, FileType type, JavaClass content) {
        this.name = name;
        this.type = type;
        this.content = content;
    }

    public boolean addChild(FileNode node) {
        if(children == null) {
            children = new ArrayList<FileNode>();
        }
        if(children.contains(node)) {
            return false;
        }
        children.add(node);
        return true;
    }
}
