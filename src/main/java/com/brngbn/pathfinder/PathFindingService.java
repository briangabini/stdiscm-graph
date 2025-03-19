package com.brngbn.pathfinder;


import com.brngbn.graph.GraphImpl;
import java.util.*;

public class PathFindingService {
    public List<GraphImpl.Edge> findPathQuery(GraphImpl graph, String queryType, String source, String dest, boolean useParallel) {
        PathFindingStrategy strategy = PathFindingStrategyFactory.getStrategy(queryType, useParallel);
        return strategy.findPath(graph, source, dest);
    }
}
