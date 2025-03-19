package com.brngbn.agent;

import com.brngbn.graph.GraphImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
/*
* This class simulates agents ( working on the nodes (resources)
* */
public class AgentSimulator {

    public static Map<String, String> occupancy = new ConcurrentHashMap<>();            // Shared occupancy map: (Node, Agent) Mapping

    public static final Object occupancyLock = new Object();                            // Lock used for synchronization

    public static void simulateAgents(GraphImpl graph, Map<String, String> initialAgentLocations) {
        occupancy.clear();
        occupancy.putAll(initialAgentLocations);
        log.info("Initial occupancy map: {}", occupancy);

        List<Thread> agentThreads = new ArrayList<>();
        for (Map.Entry<String, String> entry : initialAgentLocations.entrySet()) {
            String agentLabel = entry.getKey();
            String startNode = entry.getValue();
            Agent agent = new Agent(agentLabel, startNode, graph);
            Thread t = new Thread(agent);
            agentThreads.add(t);
            log.info("Starting thread for agent {}", agentLabel);
            t.start();
        }

        for (Thread t : agentThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                log.error("Agent thread interrupted.", e);
                Thread.currentThread().interrupt();
            }
        }
        log.info("All agents have reached their destination (or terminated). Final occupancy: {}", occupancy);
    }
}
