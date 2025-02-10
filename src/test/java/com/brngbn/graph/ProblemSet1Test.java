package com.brngbn.graph;

import com.brngbn.console.TimeMeasurer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ProblemSet1Test {

    private GraphImpl graph;
    private TimeMeasurer timeMeasurer;

    @BeforeEach
    public void setUp() throws IOException {
        GraphConfigParser.initialize();
        timeMeasurer = TimeMeasurer.getInstance();
        graph = new GraphImpl();
        graph.readGraphConfig("complete_graph_20_nodes.txt");
    }

    @Test
    public void testReadGraphConfig() throws IOException {
        GraphConfigParser parser = GraphConfigParser.getInstance();
        parser.readGraphConfig("complete_graph_20_nodes.txt");

        Map<String, List<String>> adjacencyList = parser.getAdjacencyList();

        // Verify the adjacency list is not empty
        assertThat(adjacencyList).isNotEmpty();
    }

    @Test
    public void test_EdgeQuerySerial() {

        timeMeasurer.startTracking();

        // call edge query serial
        graph.hasNodeSerial("a");

        timeMeasurer.calculateAndPrintDuration();
    }

    @Test
    public void test_NodeQuerySerialAndThreaded() {
        long totalSerialDuration = 0;
        long totalThreadedDuration = 0;

        for (int i = 0; i < 10; i++) {

            System.out.println("Iteration: " + (i + 1));

            // call node query serial
            System.out.println("Serial");
            timeMeasurer.startTracking();
            graph.hasEdgeSerial("a", "t");
            timeMeasurer.calculateAndPrintDuration();
            totalSerialDuration += timeMeasurer.calculateDuration();

            // call node query threaded
            System.out.println("Threaded");
            graph.hasEdgeThreaded("a", "t");

            System.out.println();
        }

        long avgSerialDuration = totalSerialDuration / 5;
        long avgThreadedDuration = totalThreadedDuration / 5;

        System.out.println("Average Serial Duration: " + avgSerialDuration + " ns");
        System.out.println("Average Threaded Duration: " + avgThreadedDuration + " ns");
    }
}