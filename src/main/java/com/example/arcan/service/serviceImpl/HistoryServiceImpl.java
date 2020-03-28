package com.example.arcan.service.serviceImpl;

import com.example.arcan.dao.History;
import com.example.arcan.mapper.HistoryMapper;
import com.example.arcan.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service("HistoryService")
public class HistoryServiceImpl implements HistoryService{
    @Autowired
    private HistoryMapper historyMapper;

    @Override
    public void createHistory(String repoId, String projectId) {
        Date currentTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = dateFormat.format(currentTime);

        String resultId = UUID.randomUUID().toString().replace("-","");

        History history = new History(repoId, projectId, createTime, resultId);
        historyMapper.createOneHistory(history);
    }

    @Override
    public History getRecent(String repoId) {
        return historyMapper.getHistoryList(repoId).get(0);
    }
}
