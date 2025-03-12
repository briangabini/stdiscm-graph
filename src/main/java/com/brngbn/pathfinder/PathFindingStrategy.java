package com.brngbn.pathfinder;

import com.brngbn.graph.GraphImpl;

import java.util.List;

public interface PathFindingStrategy {
    List<GraphImpl.Edge> findPath(GraphImpl graph, String source, String dest);
}

