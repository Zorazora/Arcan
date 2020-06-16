package com.example.arcan.service;

import com.example.arcan.analysis.sourcemodel.SM_Project;

public interface ProjectService {
    SM_Project readFiles(String repoId, String projectId);
    void initGraph(SM_Project project);
    void computeMetrics(SM_Project project);
}
