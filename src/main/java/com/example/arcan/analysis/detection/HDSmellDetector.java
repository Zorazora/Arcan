package com.example.arcan.analysis.detection;

import com.example.arcan.entity.Node;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HDSmellDetector extends ArchitecturalSmellDetector{
    private double threshold;

    public HDSmellDetector(String projectId) {
        super(projectId);
        this.threshold = (double) 1/4;
    }

    @Override
    public Object detectSmell() {
        ArrayList<String> results = new ArrayList<>();
        List<Node> allClassNodes = getNodeRepository().findAllClassesByProjectId(getProjectId());
        for(Node node: allClassNodes) {
            if(Math.abs(node.getFI()-node.getFO()) < threshold*(node.getFI()+node.getFO())) {
                results.add(node.getName());
            }
        }
        return results;
    }
}
