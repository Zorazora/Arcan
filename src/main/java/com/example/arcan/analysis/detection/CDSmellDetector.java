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
        List<String> newNodeNames = new ArrayList<>();

        for(String nodeName : nodeNames){
            if(nodeName.contains(".")){
                String[] strs = nodeName.split("\\.");
                newNodeNames.add(strs[strs.length-1]);
            }else{
                newNodeNames.add(nodeName);
            }
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
        result.put("nodeNames", newNodeNames);
        List<Map> list1 = new ArrayList<>();
        Map<String,Object> map1 = new HashMap();
        map1.put("title","Node Names");
        map1.put("width", 100);
        map1.put("dataIndex", "name");
        map1.put("key", "name");
        map1.put("fixed", "left");
        list1.add(map1);
        int i=0;
        for(String nodeName:newNodeNames){
            Map<String,Object> map2 = new HashMap();
            map2.put("title",nodeName);
            map2.put("dataIndex", i);
            map2.put("key", i);
            map2.put("width", 100);
            i++;
            list1.add(map2);
        }
        result.put("colums1",list1);
        List<Map> list2 = new ArrayList<>();
        for(int j=0;j<tableOne.length;j++){
            Map<String,Object> map3 = new HashMap();
            map3.put("key",j);
            map3.put("name","cycle"+j);
            for(int z=0;z<nodeNames.size();z++){
                map3.put(String.valueOf(z),tableOne[j][z]);
            }
            list2.add(map3);
        }
        result.put("data1",list2);

        List<Map> list3 = new ArrayList<>();
        Map<String,Object> map4 = new HashMap();
        map4.put("title","Node Names");
        map4.put("width", 100);
        map4.put("dataIndex", "name");
        map4.put("key", "name");
        map4.put("fixed", "left");
        list3.add(map4);
        int o=0;
        for(String nodeName:newNodeNames){
            Map<String,Object> map5 = new HashMap();
            map5.put("title",nodeName);
            map5.put("dataIndex", o);
            map5.put("key", o);
            map5.put("width", 100);
            o++;
            list3.add(map5);
        }
        result.put("colums2",list3);
        List<Map> list4 = new ArrayList<>();
        for(int j=0;j<tableTwo.length;j++){
            Map<String,Object> map5 = new HashMap();
            map5.put("key",j);
            map5.put("name",newNodeNames.get(j));
            for(int z=0;z<nodeNames.size();z++){
                map5.put(String.valueOf(z),tableTwo[j][z]);
            }
            list4.add(map5);
        }
        result.put("data2",list4);
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
