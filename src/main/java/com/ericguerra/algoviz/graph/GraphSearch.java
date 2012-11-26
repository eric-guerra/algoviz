package com.ericguerra.algoviz.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/** BFS, DFS and Dijkstra that emit a {@link GraphStep} for every meaningful event. */
public final class GraphSearch {
    public enum Kind { BFS, DFS, DIJKSTRA }

    public record Result(List<GraphStep> steps, List<Integer> path, int cost) {}

    private GraphSearch() {}

    public static Result run(Kind kind, Graph g, int source, int target) {
        return switch (kind) {
            case BFS -> bfs(g, source, target);
            case DFS -> dfs(g, source, target);
            case DIJKSTRA -> dijkstra(g, source, target);
        };
    }

    public static Result bfs(Graph g, int source, int target) {
        List<GraphStep> steps = new ArrayList<>();
        Map<Integer, Integer> parent = new HashMap<>();
        Set<Integer> visited = new LinkedHashSet<>();
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(source);
        visited.add(source);

        while (!queue.isEmpty()) {
            int node = queue.poll();
            steps.add(step("Dequeue " + node, visited, queue, null, Map.of(), List.of()));
            if (node == target) break;
            for (Graph.Edge e : g.edgesOf(node)) {
                int next = e.other(node);
                if (visited.add(next)) {
                    parent.put(next, node);
                    queue.add(next);
                    steps.add(step("Discover " + next + " from " + node, visited, queue, e, Map.of(), List.of()));
                }
            }
        }
        return finish(steps, visited, parent, source, target, g);
    }

    public static Result dfs(Graph g, int source, int target) {
        List<GraphStep> steps = new ArrayList<>();
        Map<Integer, Integer> parent = new HashMap<>();
        Set<Integer> visited = new LinkedHashSet<>();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(source);

        while (!stack.isEmpty()) {
            int node = stack.pop();
            if (!visited.add(node)) continue;
            steps.add(step("Visit " + node, visited, stack, null, Map.of(), List.of()));
            if (node == target) break;
            List<Graph.Edge> edges = new ArrayList<>(g.edgesOf(node));
            Collections.reverse(edges); // so lower-indexed neighbours are explored first
            for (Graph.Edge e : edges) {
                int next = e.other(node);
                if (!visited.contains(next)) {
                    parent.put(next, node);
                    stack.push(next);
                    steps.add(step("Push " + next, visited, stack, e, Map.of(), List.of()));
                }
            }
        }
        return finish(steps, visited, parent, source, target, g);
    }

    public static Result dijkstra(Graph g, int source, int target) {
        List<GraphStep> steps = new ArrayList<>();
        Map<Integer, Integer> dist = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();
        Set<Integer> settled = new LinkedHashSet<>();
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        dist.put(source, 0);
        pq.add(new int[]{source, 0});

        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int node = top[0];
            if (!settled.add(node)) continue; // stale queue entry
            steps.add(step("Settle " + node + " at distance " + dist.get(node), settled, frontierOf(pq), null, dist, List.of()));
            if (node == target) break;
            for (Graph.Edge e : g.edgesOf(node)) {
                int next = e.other(node);
                if (settled.contains(next)) continue;
                int candidate = dist.get(node) + e.weight();
                if (candidate < dist.getOrDefault(next, Integer.MAX_VALUE)) {
                    dist.put(next, candidate);
                    parent.put(next, node);
                    pq.add(new int[]{next, candidate});
                    steps.add(step("Relax " + node + "→" + next + " = " + candidate, settled, frontierOf(pq), e, dist, List.of()));
                }
            }
        }
        Result r = finish(steps, settled, parent, source, target, g);
        return new Result(r.steps(), r.path(), r.path().isEmpty() ? -1 : dist.get(target));
    }

    private static Set<Integer> frontierOf(PriorityQueue<int[]> pq) {
        Set<Integer> s = new HashSet<>();
        for (int[] e : pq) s.add(e[0]);
        return s;
    }

    private static GraphStep step(String msg, Set<Integer> visited, Iterable<Integer> frontier,
                                  Graph.Edge edge, Map<Integer, Integer> dist, List<Integer> path) {
        Set<Integer> f = new HashSet<>();
        frontier.forEach(f::add);
        f.removeAll(visited);
        return new GraphStep(msg, visited, f, edge, dist, path);
    }

    private static Result finish(List<GraphStep> steps, Set<Integer> visited, Map<Integer, Integer> parent,
                                 int source, int target, Graph g) {
        List<Integer> path = new ArrayList<>();
        if (source == target || parent.containsKey(target)) {
            for (Integer at = target; at != null; at = at == source ? null : parent.get(at)) path.add(at);
            Collections.reverse(path);
        }
        int cost = 0;
        for (int i = 0; i + 1 < path.size(); i++) {
            int a = path.get(i), b = path.get(i + 1);
            cost += g.edgesOf(a).stream().filter(e -> e.other(a) == b).mapToInt(Graph.Edge::weight).min().orElse(0);
        }
        String msg = path.isEmpty() ? "Target unreachable" : "Path found: " + path.size() + " nodes, cost " + cost;
        steps.add(new GraphStep(msg, visited, Set.of(), null, Map.of(), path));
        return new Result(steps, path, path.isEmpty() ? -1 : cost);
    }
}
