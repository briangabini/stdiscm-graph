package com.brngbn.graph;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.ui.view.Viewer;

import java.io.IOException;

import static org.graphstream.stream.file.FileSinkImages.LayoutPolicy;
import static org.graphstream.stream.file.FileSinkImages.Quality;

public class GraphVisualizer {

    private static final boolean saveToFile = true;

    public static void visualizeGraph(GraphImpl graph) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        Graph g = new SingleGraph("Graph");
        g.setStrict(false);
        g.setAutoCreate(true);
        g.setAttribute("ui.quality");

        // Add nodes.
        for (String node : graph.getNodeList()) {
            g.addNode(node);
            g.getNode(node).addAttribute("ui.style", "shape: circle; fill-color: yellow; size: 40px; text-alignment: center; text-style: bold; text-size: 16px;");
            g.getNode(node).addAttribute("ui.label", node);
        }

        // Add edges with weight labels.
        for (GraphImpl.FullEdge edge : graph.getEdgeList()) {
            String edgeId = edge.source + "_" + edge.destination;
            g.addEdge(edgeId, edge.source, edge.destination, true);
            g.getEdge(edgeId).addAttribute("ui.label", String.valueOf(edge.weight));
            g.getEdge(edgeId).addAttribute("ui.style", "size: 3px; text-size: 24px; text-offset: 0, -32px;");
        }

        Viewer viewer = g.display();
        viewer.enableAutoLayout();

        if (saveToFile) {
            saveGraphToFile(g, "graph.png");
        }
    }

    public static void saveGraphToFile(Graph graph, String fileName) {
        FileSinkImages fileSink = new FileSinkImages();
        fileSink.setResolution(800, 800);
        fileSink.setQuality(Quality.HIGH);
        fileSink.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
        fileSink.setAutofit(true);
        try {
            fileSink.writeAll(graph, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
