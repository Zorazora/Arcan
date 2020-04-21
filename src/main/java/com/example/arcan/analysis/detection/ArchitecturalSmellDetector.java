package com.example.arcan.analysis.detection;

import com.example.arcan.repository.NodeRepository;
import com.example.arcan.utils.SpringUtil;
import lombok.Data;

@Data
public abstract class ArchitecturalSmellDetector {
    private String projectId;
    private NodeRepository nodeRepository;

    public ArchitecturalSmellDetector(String projectId) {
        this.projectId = projectId;
        this.nodeRepository = SpringUtil.getBean(NodeRepository.class);
    }

    abstract public Object detectSmell();
}
