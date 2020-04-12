package com.example.arcan.service.serviceImpl;


import com.example.arcan.dao.Repository;
import com.example.arcan.mapper.RepositoryMapper;
import com.example.arcan.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service("RepositoryService")
public class RepositoryServiceImpl implements RepositoryService{
    @Autowired
    private RepositoryMapper repositoryMapper;

    @Override
    public String createRepository(String userId, String repoName, String description) {
        String repoId = UUID.randomUUID().toString().replace("-","");

        Date currentTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createDate = dateFormat.format(currentTime);

        Repository repository = new Repository(userId, repoId, repoName, createDate, description, "CREATED");
        repositoryMapper.createRepository(repository);
        return repoId;
    }

    @Override
    public List<Repository> getRepositoryList(String userId) {
        return repositoryMapper.findRepoByUserId(userId);
    }

    @Override
    public Repository getRepositoryById(String repoId) {
        return repositoryMapper.findRepoByRepoId(repoId);
    }

    @Override
    public boolean deleteRepository(String repoId) {
        Repository repository = repositoryMapper.findRepoByRepoId(repoId);
        if (repository != null) {
            repository.setStatus("DELETED");
            repositoryMapper.updateRepository(repository);
            return true;
        }
        return false;
    }

    @Override
    public void modifyStatus(String repoId, String status) {
        Repository repository = repositoryMapper.findRepoByRepoId(repoId);
        repository.setStatus(status);
        repositoryMapper.updateRepository(repository);
    }

    @Override
    public void modifyDescription(String repoId, String description) {
        Repository repository = repositoryMapper.findRepoByRepoId(repoId);
        repository.setDescription(description);
        repositoryMapper.updateRepository(repository);
    }
}
