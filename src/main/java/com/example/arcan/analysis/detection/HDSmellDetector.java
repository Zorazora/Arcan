package com.example.arcan.analysis.detection;

import com.example.arcan.entity.Node;
import lombok.Data;

import java.util.*;

@Data
public class HDSmellDetector extends ArchitecturalSmellDetector{
    private double threshold;

    public HDSmellDetector(String projectId) {
        super(projectId);
        this.threshold = (double) 1/4;
    }

    @Override
    public Object detectSmell() {
        ArrayList<Node> results = new ArrayList<>();
        List<Node> allClassNodes = getNodeRepository().findAllClassesByProjectId(getProjectId());
        List<Integer> ingoing = new ArrayList<>();
        List<Integer> outgoing = new ArrayList<>();
        for(Node node: allClassNodes) {
            ingoing.add(node.getFI());
            outgoing.add(node.getFO());
            if(Math.abs(node.getFI()-node.getFO()) < threshold*(node.getFI()+node.getFO())) {
                results.add(node);
            }
        }
        System.out.println(results.size());
        double inMedian = calMedian(ingoing);
        double outMedian = calMedian(outgoing);

        ArrayList<String> hubClasses = new ArrayList<>();
        ArrayList<String> test = new ArrayList<>();
        for(Node node: results) {
            test.add(node.getName());
            if(node.getFI()>inMedian && node.getFO()>outMedian) {
                hubClasses.add(node.getName());
            }
        }
        System.out.println(hubClasses.size());
        Map<String, Object> map = new HashMap<>();
        map.put("original", test);
        map.put("modified", hubClasses);
        return map;
    }

    public double calMedian(List<Integer> array) {
        Collections.sort(array);
        int size = array.size();
        double median;
        if(size%2 == 1) {
            median = array.get((size-1)/2);
        } else {
            median = (double) (array.get(size/2 - 1)+array.get(size/2))/2;
        }
        return median;
    }
}
