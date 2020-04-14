package com.example.arcan.service.serviceImpl;

import com.example.arcan.entity.Node;
import com.example.arcan.repository.NodeRepository;
import com.example.arcan.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("NodeService")
public class NodeServiceImpl implements NodeService{

    @Autowired
    private NodeRepository nodeRepository;

    @Override
    public void createNode(Node node) {
        nodeRepository.save(node);
    }
}
