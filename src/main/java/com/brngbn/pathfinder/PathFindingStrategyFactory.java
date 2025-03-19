package com.brngbn.pathfinder;

public class PathFindingStrategyFactory {
    public static PathFindingStrategy getStrategy(String queryType, boolean useParallel) {
        switch (queryType) {
            case "path":
                return useParallel ? new DfsPathThreadedStrategy() : new DfsPathStrategy();
            case "prime-path":
                return useParallel ? new PrimePathThreadedStrategy() : new PrimePathStrategy();
            case "shortest-path":
                return useParallel ? new ShortestPathThreadedStrategy() : new ShortestPathStrategy();
            case "shortest-prime-path":
                return useParallel ? new ShortestPrimePathThreadedStrategy() : new ShortestPrimePathStrategy();
            default:
                throw new IllegalArgumentException("Unknown query type: " + queryType);
        }
    }
}

