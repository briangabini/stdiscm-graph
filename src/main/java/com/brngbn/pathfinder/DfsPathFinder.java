package com.brngbn.pathfinder;

import com.brngbn.graph.GraphImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DfsPathFinder implements PathFinder {

    @Override
    public List<GraphImpl.Edge> findPath(GraphImpl graph, String source, String dest) {
        Map<String, Integer> nodeIndexMap = new HashMap<>();
        List<String> nodeList = graph.getNodeList();
        for (int i = 0; i < nodeList.size(); i++) {
            nodeIndexMap.put(nodeList.get(i), i);
        }

        int adjListSize = graph.getAdjacencyList().size();
        boolean[] visited = new boolean[adjListSize];
        List<GraphImpl.Edge> path = new ArrayList<>();

        DFSRec(graph.getAdjacencyList(),
                visited,
                nodeIndexMap.get(source),
                nodeIndexMap.get(dest),
                path,
                nodeList);

        return path;
    }

    // Recursive function for DFS traversal
    static boolean DFSRec(Map<String, List<String>> adj, boolean[] visited, int sourceIndex, int destinationIndex, List<GraphImpl.Edge> path, List<String> nodes) {
        // Mark the current vertex as visited
        visited[sourceIndex] = true;

        // If the source is the destination, return true
        if (sourceIndex == destinationIndex) {
            return true;
        }

        // Recursively visit all adjacent vertices that are not visited yet
        for (String neighbor : adj.get(nodes.get(sourceIndex))) {
            int neighborIndex = nodes.indexOf(neighbor);
            if (!visited[neighborIndex]) {
                path.add(new GraphImpl.Edge(nodes.get(sourceIndex), neighbor));
                if (DFSRec(adj, visited, neighborIndex, destinationIndex, path, nodes)) {
                    return true;
                }
                path.removeLast();
            }
        }
        return false;
    }
}