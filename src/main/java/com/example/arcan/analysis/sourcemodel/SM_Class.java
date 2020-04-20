package com.example.arcan.analysis.sourcemodel;

import com.example.arcan.service.SearchService;
import com.example.arcan.utils.SpringUtil;
import lombok.Data;
import sun.plugin2.liveconnect.JavaClass;

@Data
public class SM_Class {
    private String className;
    private JavaClass javaClass;
    private String projectId;
    private int FI;
    private int FO;
    private int CBO;
    private double LCOM;

    private SearchService searchService;

    public SM_Class(String className, JavaClass javaClass, String projectId) {
        this.className = className;
        this.javaClass = javaClass;
        this.projectId = projectId;
        this.searchService = SpringUtil.getBean(SearchService.class);
    }

    public void computeMetrics() {
        computeFI();
        computeFO();
        computeCBO();
        computeLCOM();
    }

    public void computeFI() {
        this.FI = searchService.countFanIn(className, projectId);
    }

    public void computeFO() {
        this.FO = searchService.countFanOut(className, projectId);
    }

    public void computeCBO() {

    }

    public void computeLCOM() {

    }
}
