package com.ericguerra.algoviz.graph;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Snapshot of a graph search: visited/frontier nodes, the edge just examined,
 * best-known distances (Dijkstra) and, at the end, the path to the target.
 */
public record GraphStep(
        String message,
        Set<Integer> visited,
        Set<Integer> frontier,
        Graph.Edge activeEdge,
        Map<Integer, Integer> distances,
        List<Integer> path) {

    public GraphStep {
        visited = Set.copyOf(visited);
        frontier = Set.copyOf(frontier);
        distances = Map.copyOf(distances);
        path = List.copyOf(path);
    }
}
