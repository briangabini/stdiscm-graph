package com.brngbn.graph;

public class GraphVisualizerTest {

    public final String fileUnderTest = "tc1.txt";

    public static void main(String[] args) {
        GraphImpl graph = new GraphImpl();
        try {
            graph.readGraphConfig("tc1.txt");
            GraphVisualizer.visualizeGraph(graph);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
