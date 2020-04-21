package com.example.arcan.analysis.detection;

import java.util.HashMap;
import java.util.Map;

public class Detector {
    private UDSmellDetector udSmellDetector;
    private HDSmellDetector hdSmellDetector;
    private CDSmellDetector cdSmellDetector;

    private Map<String, Object> results;

    public Detector(String projectId) {
        udSmellDetector = new UDSmellDetector(projectId);
        hdSmellDetector = new HDSmellDetector(projectId);
        cdSmellDetector = new CDSmellDetector(projectId);

        results = new HashMap<>();
    }

    public Map<String, Object> detectSmells() {
        results.put("UD", udSmellDetector.detectSmell());
        results.put("HD", hdSmellDetector.detectSmell());
        //results.put("CD", cdSmellDetector.detectSmell());
        return results;
    }
}
