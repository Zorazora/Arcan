package com.example.arcan.service;

import com.example.arcan.dao.History;

import java.util.List;

public interface HistoryService {
    void createHistory(String repoId, String projectId);
    History getRecent(String repoId);
    List<History> getHistoryListByRepoId(String repoId);
}
