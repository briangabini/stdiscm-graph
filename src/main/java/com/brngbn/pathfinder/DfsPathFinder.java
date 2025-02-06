package com.brngbn.pathfinder;

import com.brngbn.graph.GraphImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DfsPathFinder implements PathFinder {

    @Override
    public List<List<GraphImpl.Edge>> findPaths(GraphImpl graph, String source, String dest) {
        Map<String, Integer> nodeIndexMap = new HashMap<>();
        List<String> nodeList = graph.getNodeList();
        for (int i = 0; i < nodeList.size(); i++) {
            nodeIndexMap.put(nodeList.get(i), i);
        }

        int adjListSize = graph.getAdjacencyList().size();
        boolean[] visited = new boolean[adjListSize];
        List<List<GraphImpl.Edge>> allPaths = new ArrayList<>();
        List<GraphImpl.Edge> currentPath = new ArrayList<>();

        DFSRec(graph.getAdjacencyList(), visited, nodeIndexMap.get(source), nodeIndexMap.get(dest), currentPath, nodeList, allPaths);

        return allPaths;
    }

    // Recursive function for DFS traversal
    private static void DFSRec(Map<String, List<String>> adj, boolean[] visited, int sourceIndex, int destinationIndex, List<GraphImpl.Edge> currentPath, List<String> nodes, List<List<GraphImpl.Edge>> allPaths) {
        visited[sourceIndex] = true;

        if (sourceIndex == destinationIndex) {
            allPaths.add(new ArrayList<>(currentPath));
            visited[sourceIndex] = false;
            return;
        }

        // Recursively visit all adjacent nodes that are not visited yet
        for (String neighbor : adj.get(nodes.get(sourceIndex))) {
            int neighborIndex = nodes.indexOf(neighbor);
            if (!visited[neighborIndex]) {
                currentPath.add(new GraphImpl.Edge(nodes.get(sourceIndex), neighbor));
                DFSRec(adj, visited, neighborIndex, destinationIndex, currentPath, nodes, allPaths);
                currentPath.removeLast(); // Backtrack
            }
        }
        visited[sourceIndex] = false;
    }
}