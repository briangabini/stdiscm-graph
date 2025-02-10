package com.brngbn.graph;

import com.brngbn.console.TimeMeasurer;
import com.brngbn.pathfinder.DfsPathFinder;
import com.brngbn.pathfinder.DfsPathFinderThreaded;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ProblemSet1Test {

    private GraphImpl graph;
    private TimeMeasurer timeMeasurer;
    private DfsPathFinder dfsPathFinderSerial;
    private DfsPathFinderThreaded dfsPathFinderThreaded;

    @BeforeEach
    public void setUp() throws IOException {
        GraphConfigParser.initialize();
        timeMeasurer = TimeMeasurer.getInstance();
        graph = new GraphImpl();
        graph.readGraphConfig("complete_graph_10_nodes.txt");

        // init path finders
        dfsPathFinderSerial = new DfsPathFinder();
        dfsPathFinderThreaded = new DfsPathFinderThreaded();
    }

    @Test
    public void test_NodeQuerySerial() {
        // assert that n20 exists
        assertThat(graph.hasNodeSerial("n5")).isTrue();

        // assert that n99 doesn't exist
        assertThat(graph.hasNodeSerial("n99")).isFalse();
    }

    @Test
    public void test_NodeQueryThreaded() {
        // assert that n10 exists
        assertThat(graph.hasNodeThreaded("n5")).isTrue();

        // assert that n99 doesn't exist
        assertThat(graph.hasNodeThreaded("n99")).isFalse();
    }

    @Test
    public void test_EdgeQueryThreaded() {
        // assert that edge (n1, n10) exists
        assertThat(graph.hasEdgeThreaded("n1", "n5")).isTrue();

        // assert that edge (n1, n99) doesn't exist
        assertThat(graph.hasEdgeThreaded("n1", "n99")).isFalse();
    }

    @Test
    public void test_EdgeQuerySerial() {
        // assert that edge (n1, n10) exists
        assertThat(graph.hasEdgeSerial("n1", "n5")).isTrue();

        // assert that edge (n1, n99) doesn't exist
        assertThat(graph.hasEdgeSerial("n1", "n99")).isFalse();
    }

    @Test
    public void test_PathQuerySerial() {
        // assert that path n1 -> n10 exists
        List<GraphImpl.Edge> pathsToN5 = dfsPathFinderSerial.findPath(graph, "n1", "n5");
        assertThat(pathsToN5).isNotEmpty();

        // assert that path n1 -> n99 doesn't exist
        List<GraphImpl.Edge> pathsToN99 = dfsPathFinderSerial.findPath(graph, "n1", "n99");
        assertThat(pathsToN99).isEmpty();
    }

    @Test
    public void test_PathQueryThreaded() {
        // assert that path n1 -> n10 exists
        List<GraphImpl.Edge> pathsToN5 = dfsPathFinderThreaded.findPath(graph, "n1", "n5");
        assertThat(pathsToN5).isNotEmpty();

        // assert that path n1 -> n99 doesn't exist
        List<GraphImpl.Edge> pathsToN99 = dfsPathFinderThreaded.findPath(graph, "n1", "n99");
        assertThat(pathsToN99).isEmpty();
    }

    @Test
    public void testReadGraphConfig() throws IOException {
        GraphConfigParser parser = GraphConfigParser.getInstance();
        parser.readGraphConfig("complete_graph_5_nodes.txt");

        Map<String, List<String>> adjacencyList = parser.getAdjacencyList();

        // Verify the adjacency list is not empty
        assertThat(adjacencyList).isNotEmpty();
    }

    @Test
    public void test_EdgeQuerySerialAndThreaded_TimeComplexity() {
        timeMeasurer.clearDurations();

        // Loop for hasEdgeSerial
        for (int i = 0; i < 10; i++) {
            System.out.println("Serial Iteration: " + (i + 1));
            timeMeasurer.startTracking();
            graph.hasEdgeSerial("a", "t");
            timeMeasurer.addDuration(timeMeasurer.calculateDuration());
            timeMeasurer.calculateAndPrintDuration();
        }

        double avgSerialDuration = timeMeasurer.getAverageDuration();
        System.out.println("Average Serial Duration: " + avgSerialDuration + " ns");

        timeMeasurer.clearDurations();

        // Loop for hasEdgeThreaded
        for (int i = 0; i < 10; i++) {
            System.out.println("Threaded Iteration: " + (i + 1));
            timeMeasurer.startTracking();
            graph.hasEdgeThreaded("a", "t");
        }

        double avgThreadedDuration = timeMeasurer.getAverageDuration();
        System.out.println("Average Threaded Duration: " + avgThreadedDuration + " ns");
    }
}