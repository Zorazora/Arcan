package com.example.arcan.utils.graph;

import com.example.arcan.entity.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ElementaryCyclesSearch {
    /** List of cycles */
    private List cycles = null;

    /** Adjacency-list of graph */
    private int[][] adjList = null;

    /** Graphnodes */
    private List<Node> graphNodes = null;

    /** Blocked nodes, used by the algorithm of Johnson */
    private boolean[] blocked = null;

    /** B-Lists, used by the algorithm of Johnson */
    private Vector[] B = null;

    /** Stack for nodes, used by the algorithm of Johnson */
    private Vector stack = null;

    private List simplified = null;

    /**
     * Constructor.
     *
     * @param matrix adjacency-matrix of the graph
     * @param graphNodes array of the graphnodes of the graph; this is used to
     * build sets of the elementary cycles containing the objects of the original
     * graph-representation
     */
    public ElementaryCyclesSearch(boolean[][] matrix, List<Node> graphNodes) {
        this.graphNodes = graphNodes;
        this.adjList = AdjacencyList.getAdjacencyList(matrix);
    }

    /**
     * Returns List::List::Object with the Lists of nodes of all elementary
     * cycles in the graph.
     *
     * @return List::List::Object with the Lists of the elementary cycles.
     */
    public List getElementaryCycles() {
        this.cycles = new Vector();
        this.blocked = new boolean[this.adjList.length];
        this.B = new Vector[this.adjList.length];
        this.stack = new Vector();
        StrongConnectedComponents sccs = new StrongConnectedComponents(this.adjList);
        int s = 0;

        while (true) {
            SCCResult sccResult = sccs.getAdjacencyList(s);
            if (sccResult != null && sccResult.getAdjList() != null) {
                Vector[] scc = sccResult.getAdjList();
                s = sccResult.getLowestNodeId();
                for (int j = 0; j < scc.length; j++) {
                    if ((scc[j] != null) && (scc[j].size() > 0)) {
                        this.blocked[j] = false;
                        this.B[j] = new Vector();
                    }
                }

                this.findCycles(s, s, scc);
                s++;
            } else {
                break;
            }
        }

        List filtered = new ArrayList();
        for(int i = 0; i < cycles.size(); i++){
            if(((ArrayList)(cycles.get(i))).size()>2){
                filtered.add(cycles.get(i));
            }
        }

        simplified = new ArrayList();
        simplified.addAll(cycles);
        for(int i=0; i<filtered.size();i++) {
            for(int j=0; j<filtered.size();j++) {
                if(i!=j){
                    if(((ArrayList)filtered.get(j)).containsAll((ArrayList)filtered.get(i))) {
                        if(simplified.contains(filtered.get(i)) && simplified.contains(filtered.get(j))){
                            simplified.remove(filtered.get(i));
                            break;
                        }
                    }

                }
            }
        }

        return simplified;
    }

    /**
     * Calculates the cycles containing a given node in a strongly connected
     * component. The method calls itself recursivly.
     *
     * @param v
     * @param s
     * @param adjList adjacency-list with the subgraph of the strongly
     * connected component s is part of.
     * @return true, if cycle found; false otherwise
     */
    private boolean findCycles(int v, int s, Vector[] adjList) {
        boolean f = false;
        this.stack.add(new Integer(v));
        this.blocked[v] = true;

        for (int i = 0; i < adjList[v].size(); i++) {
            int w = ((Integer) adjList[v].get(i)).intValue();
            // found cycle
            if (w == s) {
                ArrayList<Node> cycle = new ArrayList<>();
                for (int j = 0; j < this.stack.size(); j++) {
                    int index = ((Integer) this.stack.get(j)).intValue();
                    cycle.add(this.graphNodes.get(index));
                }
                this.cycles.add(cycle);
                f = true;
            } else if (!this.blocked[w]) {
                if (this.findCycles(w, s, adjList)) {
                    f = true;
                }
            }
        }

        if (f) {
            this.unblock(v);
        } else {
            for (int i = 0; i < adjList[v].size(); i++) {
                int w = ((Integer) adjList[v].get(i)).intValue();
                if (!this.B[w].contains(new Integer(v))) {
                    this.B[w].add(new Integer(v));
                }
            }
        }

        this.stack.remove(new Integer(v));
        return f;
    }

    /**
     * Unblocks recursivly all blocked nodes, starting with a given node.
     *
     * @param node node to unblock
     */
    private void unblock(int node) {
        this.blocked[node] = false;
        Vector Bnode = this.B[node];
        while (Bnode.size() > 0) {
            Integer w = (Integer) Bnode.get(0);
            Bnode.remove(0);
            if (this.blocked[w.intValue()]) {
                this.unblock(w.intValue());
            }
        }
    }
}
