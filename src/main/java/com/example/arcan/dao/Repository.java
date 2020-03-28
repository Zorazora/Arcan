package com.example.arcan.dao;

import lombok.Data;

@Data
public class Repository {
    private String userId;
    private String repoId;
    private String repoName;
    private String createDate;
    private String description;
    private String status;

    public Repository(String userId, String repoId, String repoName,
                      String createDate, String description, String status) {
        this.userId = userId;
        this.repoId = repoId;
        this.repoName = repoName;
        this.createDate = createDate;
        this.description = description;
        this.status = status;
    }
}
