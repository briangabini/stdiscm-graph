package com.brngbn.graph;

public class GraphVisualizerTest {

    public static final String fileUnderTest = "weighted_custom_tc2.txt";

    public static void main(String[] args) {
        GraphImpl graph = new GraphImpl();
        try {
            graph.readGraphConfig(fileUnderTest);
            GraphVisualizer.visualizeGraph(graph);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
