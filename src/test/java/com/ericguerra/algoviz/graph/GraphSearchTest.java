package com.ericguerra.algoviz.graph;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphSearchTest {
    /**
     * 0 --1-- 1 --1-- 2
     * |               |
     * 10              1
     * |               |
     * 3 ------1------ 4
     */
    private static Graph sample() {
        Graph g = new Graph();
        for (int i = 0; i < 5; i++) g.addNode(i, 0);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 4, 1);
        g.addEdge(0, 3, 10);
        g.addEdge(3, 4, 1);
        return g;
    }

    @Test
    void bfsFindsFewestHops() {
        var result = GraphSearch.bfs(sample(), 0, 4);
        assertEquals(List.of(0, 3, 4), result.path());
        assertEquals(11, result.cost());
    }

    @Test
    void dijkstraFindsCheapestPath() {
        var result = GraphSearch.dijkstra(sample(), 0, 4);
        assertEquals(List.of(0, 1, 2, 4), result.path());
        assertEquals(3, result.cost());
    }

    @Test
    void dfsFindsSomePath() {
        var result = GraphSearch.dfs(sample(), 0, 4);
        assertEquals(0, result.path().get(0));
        assertEquals(4, result.path().get(result.path().size() - 1));
    }

    @Test
    void unreachableTargetYieldsEmptyPath() {
        Graph g = sample();
        int island = g.addNode(9, 9);
        for (var kind : GraphSearch.Kind.values()) {
            var result = GraphSearch.run(kind, g, 0, island);
            assertTrue(result.path().isEmpty(), kind.name());
            assertEquals(-1, result.cost());
            assertEquals("Target unreachable", result.steps().get(result.steps().size() - 1).message());
        }
    }

    @Test
    void sourceEqualsTarget() {
        var result = GraphSearch.dijkstra(sample(), 2, 2);
        assertEquals(List.of(2), result.path());
        assertEquals(0, result.cost());
    }

    @Test
    void randomGraphIsConnected() {
        Graph g = Graph.random(8, 5, 7L);
        for (int target = 1; target < g.size(); target++) {
            assertFalse(GraphSearch.bfs(g, 0, target).path().isEmpty(), "node " + target + " unreachable");
        }
    }

    @Test
    void duplicateAndSelfEdgesAreIgnored() {
        Graph g = new Graph();
        g.addNode(0, 0);
        g.addNode(1, 1);
        g.addEdge(0, 1, 3);
        g.addEdge(1, 0, 5);
        g.addEdge(0, 0, 1);
        assertEquals(1, g.edges().size());
        assertThrows(IllegalArgumentException.class, () -> g.addEdge(0, 1, -1));
    }
}
