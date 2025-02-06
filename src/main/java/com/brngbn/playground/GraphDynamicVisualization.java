package com.brngbn.playground;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import org.graphstream.ui.view.Viewer;

import java.io.IOException;

public class GraphDynamicVisualization {

    public static void main(String[] args) {
        Graph graph = new SingleGraph("Graph Stream Example");

        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

        simulateGraphStream(graph);
    }

    private static void simulateGraphStream(Graph graph) {
        // add initial nodes and edges
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");

        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");

        // Simulate a stream of new edges
        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(1000); // Simulate a delay between events
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Add a new node and edge dynamically
            String newNodeId = "N" + i;
            graph.addNode(newNodeId);
            graph.addEdge("A" + newNodeId, "A", newNodeId);

            System.out.println("Added node: " + newNodeId + " and edge: A-" + newNodeId);
        }
    }

    private static void loadGraphFromFile(Graph graph, String filePath) {
        try {
            FileSource fileSource = FileSourceFactory.sourceFor(filePath);
            fileSource.addSink(graph);
            fileSource.readAll(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}