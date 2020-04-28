package com.example.arcan.utils.graph;

import com.example.arcan.entity.Node;
import lombok.Data;

import java.util.List;

@Data
public class Graph {
    private List<Node> vertices;
    private boolean[][] adjacencyMatrix;

    public Graph(List<Node> vertices) {
        this.vertices = vertices;
        this.adjacencyMatrix = new boolean[vertices.size()][vertices.size()];
    }

    public boolean addLine(Node node1, Node node2) {
        int startIndex = vertices.indexOf(node1);
        int endIndex = vertices.indexOf(node2);
        if(startIndex!=-1 && endIndex!=-1) {
            adjacencyMatrix[startIndex][endIndex] = true;
            return true;
        }
        return false;
    }

    public List findAllCycles() {
        ElementaryCyclesSearch cyclesSearch = new ElementaryCyclesSearch(adjacencyMatrix, vertices);
        return cyclesSearch.getElementaryCycles();
    }
}
