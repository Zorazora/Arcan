package com.example.arcan.dao;

import lombok.Data;

@Data
public class Repository {
    private String userId;
    private String repoId;
    private String repoName;
    private String createDate;
    private String status;
}
