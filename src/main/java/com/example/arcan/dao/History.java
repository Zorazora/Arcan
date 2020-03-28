package com.example.arcan.dao;

import lombok.Data;

@Data
public class History {
    private String repoId;
    private String projectId;
    private String createTime;
    private String resultId;

    public History(String repoId, String projectId, String createTime, String resultId) {
        this.repoId = repoId;
        this.projectId = projectId;
        this.createTime = createTime;
        this.resultId = resultId;
    }
}
