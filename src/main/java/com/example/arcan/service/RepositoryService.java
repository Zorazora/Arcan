package com.example.arcan.service;

import com.example.arcan.dao.Repository;

import java.util.List;

public interface RepositoryService {
    String createRepository(String userId, String repoName, String description);
    List<Repository> getRepositoryList(String userId);
    Repository getRepositoryById(String repoId);
    boolean deleteRepository(String repoId);
    void modifyStatus(String repoId, String status);
    void modifyDescription(String repoId, String description);
    List<Repository> findFitKeyWord(String userId, String keyword);
}
