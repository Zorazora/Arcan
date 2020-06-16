package com.example.arcan.analysis.detection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Detector {
    //private List<UDSmellDetector> udSmellDetectors;
    private UDSmellDetector udSmellDetector;
    private HDSmellDetector hdSmellDetector;
    private CDSmellDetector cdSmellDetector;

    private Map<String, Object> results;

    public Detector(String projectId) {
//        udSmellDetectors = new ArrayList<>();
//        UDSmellDetector tmp;
//        for(int i=0;i<9;i++) {
//            tmp = new UDSmellDetector(projectId, (i+1)*0.1);
//            udSmellDetectors.add(tmp);
//        }
        udSmellDetector = new UDSmellDetector(projectId, 0.3);
        hdSmellDetector = new HDSmellDetector(projectId);
        cdSmellDetector = new CDSmellDetector(projectId);

        results = new HashMap<>();
    }

    public Map<String, Object> detectSmells() {
        Map<Double, Object> result = new HashMap<>();
//        for(UDSmellDetector udSmellDetector: udSmellDetectors) {
//            result.put(udSmellDetector.getThreshold(), udSmellDetector.detectSmell());
//        }
//        results.put("UD", result);
        results.put("UD", udSmellDetector.detectSmell());
        results.put("HD", hdSmellDetector.detectSmell());
        results.put("CD", cdSmellDetector.detectSmell());
        return results;
    }
}
