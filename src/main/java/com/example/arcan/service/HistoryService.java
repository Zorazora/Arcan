package com.example.arcan.service;

import com.example.arcan.dao.History;

public interface HistoryService {
    void createHistory(String repoId, String projectId);
    History getRecent(String repoId);
}
