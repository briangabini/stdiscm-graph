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

        timeMeasurer.calculateStartTime();

        // call edge query serial
        graph.hasNodeSerial("a");

        timeMeasurer.calculateEndTimeAndDuration();
    }

    @Test
    public void test_NodeQuerySerialAndThreaded() {

        // call node query serial
        timeMeasurer.calculateStartTime();
        graph.hasEdgeSerial("a", "t");
        timeMeasurer.calculateEndTimeAndDuration();

        // call node query threaded
        graph.hasEdgeThreaded("a", "t");
    }
}