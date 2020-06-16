package com.example.arcan.analysis.sourcemodel;

import lombok.Data;

import java.util.ArrayList;

@Data
public class SM_Project {
    private ArrayList<SM_Package> packages;
    private ArrayList<SM_Class> classes;
    private ArrayList<String> packageNames;
    private ArrayList<String> classNames;
    private String projectId;

    public SM_Project(String projectId) {
        this.projectId = projectId;
        this.packages = new ArrayList<>();
        this.classes = new ArrayList<>();
        this.packageNames = new ArrayList<>();
        this.classNames = new ArrayList<>();
    }

    public void addClass(SM_Class clazz) {
        classes.add(clazz);
    }

    public void addPackage(SM_Package sm_package) {
        packages.add(sm_package);
    }
}
