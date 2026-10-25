# AlgoViz

A desktop app that animates sorting and graph search algorithms one step at a time. It's written in **Java Swing**.

> Portfolio reference implementation of a 2010 university project, built using Java 17 language features (records, switch expressions).

## Features

**Sorting:** bubble, selection, insertion, merge, quick (median-of-three, bounded recursion depth) and heap sort.
- Each algorithm runs against a `SortRecorder` that records every compare, swap and write as an immutable frame.
- Shows live comparison and write counters next to each algorithm's complexity.
- Color legend: amber = compare, red = swap/write, green = in final position.

**Graph search:** BFS, DFS and Dijkstra on a random connected weighted graph.
- Click a node to set the source, shift-click to set the target.
- Shows the frontier, visited and settled nodes, edge relaxation, live distance labels and the final path with its cost.

**Playback:** play/pause, step forward and back, and a speed slider. Because the algorithm has already run in full, stepping back is free.

## Design

Algorithms never touch the UI. They produce a `List<Step>`, and a generic `Player<T>` drives any panel from that list on a Swing `Timer`. This keeps the algorithms unit-testable and makes scrubbing back and forth trivial.

```
sort/   SortAlgorithm, SortRecorder, SortStep, 6 implementations
graph/  Graph, GraphSearch (BFS/DFS/Dijkstra), GraphStep
ui/     Player, SortPanel, GraphPanel, Controls
```

## Run

### Requirements

- JDK 17 or newer to compile and run the app.
- Apache Maven installed and available as `mvn` on your `PATH`.
- A graphical desktop session to display the Swing UI.

Check `java -version` and `mvn -version` before building. Maven must also use JDK 17 or newer.

Run these commands from the repository root (the directory containing `pom.xml`):

```bash
mvn package
java -jar target/algoviz-1.0.0.jar
```

## Test

```bash
mvn test
```

The tests check every sort against `Arrays.sort` on edge cases and 50 random arrays each, verify bubble sort's early exit and quicksort's behavior on already-sorted input, and cover BFS hop count against Dijkstra's cheapest path, unreachable targets and graph connectivity.
