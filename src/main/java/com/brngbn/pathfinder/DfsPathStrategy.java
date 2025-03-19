package com.brngbn.pathfinder;

import com.brngbn.graph.GraphImpl;

import java.util.*;

public class DfsPathStrategy implements PathFindingStrategy {

    @Override
    public List<GraphImpl.Edge> findPath(GraphImpl graph, String source, String dest) {
        Set<String> visited = new HashSet<>();
        List<GraphImpl.Edge> result = dfsFindPath(graph, source, dest, visited, new ArrayList<>());
        return result == null ? new ArrayList<>() : result;
    }

    private List<GraphImpl.Edge> dfsFindPath(GraphImpl graph, String current, String dest,
                                             Set<String> visited, List<GraphImpl.Edge> path) {
        if (current.equals(dest)) {
            return new ArrayList<>(path);
        }
        visited.add(current);
        List<GraphImpl.Edge> edges = graph.getAdjacencyList().getOrDefault(current, new LinkedList<>());
        for (GraphImpl.Edge edge : edges) {
            if (!visited.contains(edge.neighbor)) {
                path.add(edge);
                List<GraphImpl.Edge> result = dfsFindPath(graph, edge.neighbor, dest, visited, path);
                if (result != null)
                    return result;
                path.remove(path.size() - 1);
            }
        }
        visited.remove(current);
        return null;
    }
}
