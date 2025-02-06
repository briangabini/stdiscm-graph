package com.brngbn.pathfinder;

import com.brngbn.graph.GraphImpl;

import java.util.List;

public interface PathFinder {
    public List<List<GraphImpl.Edge>> findPaths(GraphImpl graph, String source, String destination);
}