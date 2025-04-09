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

    private int stuckAttempts = 0;

    private String previousNode = null;

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
        log.info("Agent {} starting run loop at node {}", label, currentNode);

        while (!currentNode.equals(destination)) {
            List<GraphImpl.Edge> validEdges = getValidEdges();

            log.info("Agent {} at node {} found valid moves: {}", label, currentNode, validEdges);
            if (!hasValidMoves(validEdges)) break;
            shuffleEdges(validEdges);

            boolean moved = false;
            for (GraphImpl.Edge edge : validEdges) {
                String target = edge.neighbor;
                moved = occupyValidNode(target);
                if (moved) break;
            }

            if (!moved) {
                stuckAttempts++;
                // After 3 consecutive failed attempts, try to backtrack
                if (stuckAttempts >= 3) {
                    boolean backtracked = backtrack();
                    if (backtracked) {
                        stuckAttempts = 0; // Reset counter on successful backtracking
                        log.info("Agent {} successfully backtracked to {}.", label, currentNode);
                        // Longer randomized backoff after backtracking.
                        randomBackoffSleep();
                        continue;
                    }
                }
            } else {
                stuckAttempts = 0;
            }

            try {
                sleepAfterAction(moved);
            } catch(InterruptedException e) {
                log.error("Error from run() - {}", e.toString());
            }
        }

        handleAgentTermination();
    }

    private boolean hasValidMoves(List<GraphImpl.Edge> validEdges) {
        if (validEdges.isEmpty()) {
            log.warn("Agent {} at node {} has no valid moves. Stuck!", label, currentNode);
            return false;
        }
        return true;
    }

    private static void shuffleEdges(List<GraphImpl.Edge> validEdges) {
        Random random = new Random();
        Collections.shuffle(validEdges, random);            // Select a node randomly, based on specs, edge at index 0 will be chosen
    }

    private boolean occupyValidNode(String target) {

        synchronized (AgentSimulator.occupancyLock) {
            if (!AgentSimulator.occupancy.containsKey(target)) {
                log.info("Agent {} moving from {} to {}", label, currentNode, target);
                // Move the agent: free the current node and occupy the target node.
                previousNode = currentNode;
                AgentSimulator.occupancy.remove(currentNode);
                AgentSimulator.occupancy.put(target, label);
                currentNode = target;
                return true;
            } else {
                log.info("Agent {} cannot move to {} because it is occupied by {}", label, target, AgentSimulator.occupancy.get(target));
                return false;
            }
        }
    }

    private boolean backtrack() {
        if (previousNode == null || previousNode.equals(currentNode)) {
            log.info("Agent {} has no previous node to backtrack to.", label);
            return false;
        }
        synchronized (AgentSimulator.occupancyLock) {
            if (!AgentSimulator.occupancy.containsKey(previousNode)) {
                log.info("Agent {} backtracking from {} to previous node {}", label, currentNode, previousNode);
                AgentSimulator.occupancy.remove(currentNode);
                AgentSimulator.occupancy.put(previousNode, label);
                currentNode = previousNode;
                previousNode = null;
                return true;
            } else {
                log.info("Agent {} attempted to backtrack from {} to {} but it is still occupied.", label, currentNode, previousNode);
                return false;
            }
        }
    }

    private void sleepAfterAction(boolean moved) throws InterruptedException {
        if (!moved) {

            log.info("Agent {} did not move from {} as all valid moves are occupied. Waiting...", label, currentNode);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("Agent {} interrupted while waiting.", label, e);
                Thread.currentThread().interrupt();
            }
        } else {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("Agent {} interrupted after moving.", label, e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private void randomBackoffSleep() {
        try {
            long sleepTime = 1500 + new Random().nextInt(1000);
            log.info("Agent {} backing off for {} ms after backtracking.", label, sleepTime);
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            log.error("Agent {} interrupted during backoff sleep.", label, e);
            Thread.currentThread().interrupt();
        }
    }

    private void handleAgentTermination() {
        if (currentNode.equals(destination)) {
            synchronized (AgentSimulator.occupancyLock) {
                AgentSimulator.occupancy.remove(currentNode);
            } log.info("Agent {} reached its destination {} and is removed from the graph.", label, destination);
        } else {
            log.info("Agent {} terminated without reaching its destination.", label);
        }
    }

    private List<GraphImpl.Edge> getValidEdges() {
        List<GraphImpl.Edge> edges = graph.getAdjacencyList().get(currentNode);
        List<GraphImpl.Edge> validEdges = new ArrayList<>();
        if (edges != null) {
            for (GraphImpl.Edge edge : edges) {
                if (edge.weight == allowedWeight) {
                    validEdges.add(edge);
                }
            }
        }
        return validEdges;
    }
}