package com.brngbn.graph;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;

public class GraphVisualizer {

    public static void visualizeGraph(GraphImpl graph) {
        Graph g = new SingleGraph("Graph");
        g.setStrict(false);
        g.setAutoCreate(true);
        g.setAttribute("ui.quality");

        for (String node : graph.getNodeList()) {
            g.addNode(node);
            g.getNode(node).addAttribute("ui.style", "shape:circle;fill-color: yellow;size: 30px; text-alignment: center;");
            g.getNode(node).addAttribute("ui.label", node);
        }

        for (GraphImpl.Edge edge : graph.getEdgeList()) {
            g.addEdge(edge.source() + edge.destination(), edge.source(), edge.destination(), true);
        }

        Viewer viewer = g.display();
        viewer.enableAutoLayout();
    }

    public static void main(String args[]) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        Graph graph = new SingleGraph("Tutorial 1");
        graph.setStrict(false);
        graph.setAutoCreate( true );
        graph.setAttribute("ui.quality");

        // create nodes
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addNode("D");
        graph.addNode("E");
        graph.addNode("F");

        // add edges
        graph.addEdge("AB", "A", "B", true);
        graph.addEdge("AD", "A", "D", true);
        graph.addEdge("AE", "A", "E", true);
        graph.addEdge("BC", "B", "C", true);
        graph.addEdge("BF", "B", "F", true);
        graph.addEdge("CA", "C", "A", true);
        graph.addEdge("CF", "C", "F", true);
        graph.addEdge("DE", "D", "E", true);
        graph.addEdge("DG", "D", "G", true);
        graph.addEdge("EF", "E", "F", true);
        graph.addEdge("EG", "E", "G", true);
        graph.addEdge("FG", "F", "G", true);
        graph.addEdge("GA", "G", "A", true);

        // Set the style for the nodes
        for (String nodeId : new String[]{"A", "B", "C", "D", "E", "F", "G"}) {
            graph.getNode(nodeId).addAttribute("ui.style", "shape:circle;fill-color: yellow;size: 30px; text-alignment: center;");
            graph.getNode(nodeId).addAttribute("ui.label", nodeId);
        }

        Viewer viewer = graph.display();
        viewer.enableAutoLayout();
    }
}
