package com.example.arcan.analysis.detection;

import com.example.arcan.entity.Node;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class UDSmellDetector extends ArchitecturalSmellDetector implements Serializable {
    private double threshold;

    public UDSmellDetector(String projectId, double threshold) {
        super(projectId);
        //this.threshold = (double) 1/3;
        this.threshold = threshold;
    }

    @Override
    public Object detectSmell() {
        ArrayList<Dependency> results = new ArrayList<>();
        List<Node> allPackageNode = getNodeRepository().findNodesByProjectIdAndModifier(getProjectId(), "PACKAGE");
        Dependency dependency;
        for(Node node: allPackageNode) {
            dependency = new Dependency(node.getName(), node.getRMI());
            List<Node> afferentNodes = getNodeRepository().getAfferentPackage(node.getName(), node.getProjectId());
            dependency.setTotal(afferentNodes.size());
            for(Node afferentNode: afferentNodes) {
                if(afferentNode.getRMI()>node.getRMI()) {
                    dependency.addBad(afferentNode.getName(), afferentNode.getRMI());
                }
            }
            if(dependency.getCauseSmellPackages().size()!=0) {
                dependency.computeFiltered();
                results.add(dependency);
            }
        }

        List<Dependency> result = results.stream().filter(d->d.filtered==false).collect(Collectors.toList());
        return result;
    }

    @Data
    class Dependency implements Serializable{
        private String packageName;
        private double instability;
        private Map<String, Double> causeSmellPackages;
        private int total;
        private boolean filtered;
        public Dependency(String packageName, double instability){
            this.packageName = packageName;
            this.instability = instability;
            this.causeSmellPackages = new HashMap<>();
            total = 0;
        }
        public void addBad(String causeName, double RMI) {
            causeSmellPackages.put(causeName, RMI);
        }

        public void computeFiltered() {
            double value = (double) causeSmellPackages.size()/total;
            this.filtered = !(value > threshold);
        }
    }
}
