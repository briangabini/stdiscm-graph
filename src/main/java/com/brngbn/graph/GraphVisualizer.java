package com.brngbn.graph;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;

public class GraphVisualizer {

    public static void visualizeGraph(GraphImpl graph) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        Graph g = new SingleGraph("Graph");
        g.setStrict(false);
        g.setAutoCreate(true);
        g.setAttribute("ui.quality");

        for (String node : graph.getNodeList()) {
            g.addNode(node);
            g.getNode(node).addAttribute("ui.style", "shape: circle; fill-color: yellow; size: 40px; text-alignment: center; text-style: bold; text-size: 16px;");
            g.getNode(node).addAttribute("ui.label", node);
        }

        for (GraphImpl.Edge edge : graph.getEdgeList()) {
            g.addEdge(edge.source() + edge.destination(), edge.source(), edge.destination(), true);
            g.getEdge(edge.source() + edge.destination()).addAttribute("ui.style", "size: 3px;");
        }

        Viewer viewer = g.display();
        viewer.enableAutoLayout();
    }
}
