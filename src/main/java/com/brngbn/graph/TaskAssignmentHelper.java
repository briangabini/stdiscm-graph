package com.brngbn.graph;

import java.util.*;
import java.util.concurrent.Callable;

public class TaskAssignmentHelper {
    private static final int MAX_THREADS = 6;

    public static List<Callable<Boolean>> assignNodeTasks(Map<String, LinkedList<GraphImpl.Edge>> adjacencyList, String node) {
        List<String> nodes = new ArrayList<>(adjacencyList.keySet());
        int chunkSize = (int) Math.ceil((double) nodes.size() / MAX_THREADS);
        List<Callable<Boolean>> tasks = new ArrayList<>();

        for (int i = 0; i < nodes.size(); i += chunkSize) {
            int start = i;
            int end = Math.min(i + chunkSize, nodes.size());
            System.out.println("Task checking nodes from index " + start + " to " + end);
            tasks.add(() -> {
                for (int j = start; j < end; j++) {
                    if (nodes.get(j).equals(node)) {
                        System.out.println("Found node: " + node);
                        return true;
                    }
                }
                return false;
            });
        }
        return tasks;
    }

    public static List<Callable<Boolean>> assignEdgeTasks(Map<String, LinkedList<GraphImpl.Edge>> adjacencyList, String source, String destination) {
        List<Map.Entry<String, LinkedList<GraphImpl.Edge>>> entries = new ArrayList<>(adjacencyList.entrySet());
        int chunkSize = (int) Math.ceil((double) entries.size() / MAX_THREADS);
        List<Callable<Boolean>> tasks = new ArrayList<>();

        for (int i = 0; i < entries.size(); i += chunkSize) {
            int start = i;
            int end = Math.min(i + chunkSize, entries.size());
            tasks.add(() -> {
                for (int j = start; j < end; j++) {
                    Map.Entry<String, LinkedList<GraphImpl.Edge>> entry = entries.get(j);
                    if (entry.getKey().equals(source)) {
                        for (GraphImpl.Edge edge : entry.getValue()) {
                            if (edge.neighbor.equals(destination)) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            });
        }
        return tasks;
    }
}
