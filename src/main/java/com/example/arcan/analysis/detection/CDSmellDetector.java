package com.example.arcan.analysis.detection;

import com.example.arcan.entity.Node;
import com.example.arcan.utils.graph.Graph;
import java.util.*;
import java.util.stream.Collectors;

public class CDSmellDetector extends ArchitecturalSmellDetector{

    private List<Node> packageNodes;
    private List<Node> classNodes;
    public CDSmellDetector(String projectId) {
        super(projectId);
    }

    @Override
    public Object detectSmell() {
        Map<String, Object> map = new HashMap<>();
        map.put("package", formatOutput(packageLevelDetect(), "PACKAGE"));
        map.put("class", formatOutput(classLevelDetect(), "CLASS"));
//        map.put("package", packageLevelDetect().size());
//        map.put("class", classLevelDetect().size());
        return map;
    }

    private Object formatOutput(List cycles, String type) {
        int[][] tableOne;
        int[][] tableTwo;
        List<String> nodeNames;
        if(type.equals("PACKAGE")) {
            tableOne = new int[cycles.size()][packageNodes.size()];
            tableTwo = new int[packageNodes.size()][packageNodes.size()];
            nodeNames = packageNodes.stream().map(Node::getName).collect(Collectors.toList());
        }else {
            tableOne = new int[cycles.size()][classNodes.size()];
            tableTwo = new int[classNodes.size()][classNodes.size()];
            nodeNames = classNodes.stream().map(Node::getName).collect(Collectors.toList());
        }
        Map<Integer, Set> map = new HashMap<>();
        for(int i=0; i<cycles.size(); i++) {
            List cycle = (List) cycles.get(i);
            for(int j=0; j<cycle.size(); j++) {
                Node node = (Node) cycle.get(j);
                int index;
                if(type.equals("PACKAGE")) {
                    index = packageNodes.indexOf(node);
                }else {
                    index = classNodes.indexOf(node);
                }
                if(index != -1) {
                    map.computeIfAbsent(index, k -> new HashSet());
                    map.get(index).add(i);
                    tableOne[i][index] = 1;
                }
            }

        }
        for(int i=0; i<tableOne[0].length; i++) {
            for(int j=0; j<tableOne[0].length; j++) {
                if(map.get(i)==null || map.get(j)== null) {
                    tableTwo[i][j] = 0;
                }else {
                    if(i==j) {
                        tableTwo[i][j]=map.get(i).size();
                    }else if(i>j) {
                        tableTwo[i][j] = tableTwo[j][i];
                    }else {
                        Set<Integer> set = new HashSet<>(map.get(i));
                        set.retainAll(map.get(j));
                        tableTwo[i][j] = set.size();
                    }
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("tableOne", tableOne);
        result.put("tableTwo", tableTwo);
        result.put("nodeNames", nodeNames);
        return result;
    }

    private List packageLevelDetect() {
        packageNodes = getNodeRepository().findNodesByProjectIdAndModifier(getProjectId(), "PACKAGE");
        Graph packageSubGraph = new Graph(packageNodes);
        for (Node node: packageNodes) {
            List<Node> dependencyNodes = getNodeRepository().findDependencyPackage(node.getName(), getProjectId());
            for(Node dependency: dependencyNodes) {
                packageSubGraph.addLine(node, dependency);
            }
        }
        return packageSubGraph.findAllCycles();
    }

    private List classLevelDetect() {
        classNodes = getNodeRepository().findAllClassesByProjectId(getProjectId());
        Graph classSubGraph = new Graph(classNodes);
        for (Node node: classNodes) {
            List<Node> dependencyNodes = getNodeRepository().findDependencyNode(node.getName(), getProjectId());
            for(Node dependency: dependencyNodes) {
                classSubGraph.addLine(node, dependency);
            }
        }
        return classSubGraph.findAllCycles();
    }

}
