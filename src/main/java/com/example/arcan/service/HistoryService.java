package com.example.arcan.service;

import com.example.arcan.dao.History;

import java.util.List;

public interface HistoryService {
    /**
     * 返回resultId
     * @param repoId
     * @param projectId
     * @return
     */
    String createHistory(String repoId, String projectId);
    History getRecent(String repoId);
    List<History> getHistoryListByRepoId(String repoId);
}
