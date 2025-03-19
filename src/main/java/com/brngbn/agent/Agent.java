package com.brngbn.agent;

import com.brngbn.graph.GraphImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class Agent implements Runnable {
    private final String label;
    private String currentNode;
    private final String destination;
    private final int allowedWeight;
    private final GraphImpl graph;

    public Agent(String label, String startNode, GraphImpl graph) {
        this.label = label;
        this.currentNode = startNode;
        this.graph = graph;

        // For an agent "x-y", destination is "x" and only edges with weight x are allowed.
        String[] parts = label.split("-");
        this.destination = parts[0];
        this.allowedWeight = Integer.parseInt(parts[0]);
        log.info("Created agent {} at starting node {} with destination {} (allowed weight {})", label, startNode, destination, allowedWeight);
    }

    @Override
    public void run() {
        Random random = new Random();
        log.info("Agent {} starting run loop at node {}", label, currentNode);
        while (!currentNode.equals(destination)) {
            List<GraphImpl.Edge> edges = graph.getAdjacencyList().get(currentNode);
            List<GraphImpl.Edge> validEdges = new ArrayList<>();
            if (edges != null) {
                for (GraphImpl.Edge edge : edges) {
                    if (edge.weight == allowedWeight) {
                        validEdges.add(edge);
                    }
                }
            }
            log.info("Agent {} at node {} found valid moves: {}", label, currentNode, validEdges);
            if (validEdges.isEmpty()) {
                log.warn("Agent {} at node {} has no valid moves. Stuck!", label, currentNode);
                break;
            }

            Collections.shuffle(validEdges, random);
            boolean moved = false;

            for (GraphImpl.Edge edge : validEdges) {
                String target = edge.neighbor;
                // Synchronize on the shared occupancy lock to check and update node occupancy.
                synchronized (AgentSimulator.occupancyLock) {
                    if (!AgentSimulator.occupancy.containsKey(target)) {
                        log.info("Agent {} moving from {} to {}", label, currentNode, target);
                        // Move the agent: free the current node and occupy the target node.
                        AgentSimulator.occupancy.remove(currentNode);
                        AgentSimulator.occupancy.put(target, label);
                        currentNode = target;
                        moved = true;
                        break;
                    } else {
                        log.info("Agent {} cannot move to {} because it is occupied by {}", label, target, AgentSimulator.occupancy.get(target));
                    }
                }
            }
            if (!moved) {
                log.info("Agent {} did not move from {} as all valid moves are occupied. Waiting...", label, currentNode);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("Agent {} interrupted while waiting.", label, e);
                    Thread.currentThread().interrupt();
                    break;
                }
            } else {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("Agent {} interrupted after moving.", label, e);
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        if (currentNode.equals(destination)) {
            synchronized (AgentSimulator.occupancyLock) {
                AgentSimulator.occupancy.remove(currentNode);
            }
            log.info("Agent {} reached its destination {} and is removed from the graph.", label, destination);
        } else {
            log.info("Agent {} terminated without reaching its destination.", label);
        }
    }
}
