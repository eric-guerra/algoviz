package com.ericguerra.algoviz.graph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Undirected weighted graph with node positions for drawing. */
public final class Graph {
    public record Edge(int from, int to, int weight) {
        public int other(int node) {
            return node == from ? to : from;
        }
    }

    private final List<Point2D.Double> positions = new ArrayList<>();
    private final List<List<Edge>> adjacency = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();

    public int addNode(double x, double y) {
        positions.add(new Point2D.Double(x, y));
        adjacency.add(new ArrayList<>());
        return positions.size() - 1;
    }

    public void addEdge(int a, int b, int weight) {
        if (weight < 0) throw new IllegalArgumentException("Negative weights are not supported");
        if (a == b || hasEdge(a, b)) return;
        Edge e = new Edge(a, b, weight);
        edges.add(e);
        adjacency.get(a).add(e);
        adjacency.get(b).add(e);
    }

    public boolean hasEdge(int a, int b) {
        return adjacency.get(a).stream().anyMatch(e -> e.other(a) == b);
    }

    public int size() {
        return positions.size();
    }

    public List<Edge> edgesOf(int node) {
        return Collections.unmodifiableList(adjacency.get(node));
    }

    public List<Edge> edges() {
        return Collections.unmodifiableList(edges);
    }

    public Point2D.Double position(int node) {
        return positions.get(node);
    }

    /**
     * Random connected graph laid out on a jittered grid. A spanning chain guarantees
     * connectivity; extra edges link nearby nodes so the drawing stays readable.
     */
    public static Graph random(int cols, int rows, long seed) {
        Random rnd = new Random(seed);
        Graph g = new Graph();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                g.addNode((c + 0.5 + (rnd.nextDouble() - 0.5) * 0.5) / cols,
                          (r + 0.5 + (rnd.nextDouble() - 0.5) * 0.5) / rows);
            }
        }
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int id = r * cols + c;
                // Every node except the origin links back to its left or upper neighbour,
                // which forms a spanning tree and guarantees connectivity.
                if (r == 0 && c > 0) g.addEdge(id, id - 1, 1 + rnd.nextInt(9));
                else if (c == 0 && r > 0) g.addEdge(id, id - cols, 1 + rnd.nextInt(9));
                else if (r > 0) g.addEdge(id, rnd.nextBoolean() ? id - 1 : id - cols, 1 + rnd.nextInt(9));

                if (c + 1 < cols && rnd.nextDouble() < 0.5) g.addEdge(id, id + 1, 1 + rnd.nextInt(9));
                if (r + 1 < rows && rnd.nextDouble() < 0.5) g.addEdge(id, id + cols, 1 + rnd.nextInt(9));
                if (c + 1 < cols && r + 1 < rows && rnd.nextDouble() < 0.2) g.addEdge(id, id + cols + 1, 1 + rnd.nextInt(9));
            }
        }
        return g;
    }
}
