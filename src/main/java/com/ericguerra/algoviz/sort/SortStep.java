package com.ericguerra.algoviz.sort;

import java.util.Set;

/**
 * One frame of a sorting animation: what happened, which indices were involved,
 * the array state after the operation, and which positions are final.
 */
public record SortStep(Type type, int i, int j, int[] values, Set<Integer> sorted) {

    public enum Type { COMPARE, SWAP, WRITE, MARK_SORTED, DONE }

    public SortStep {
        values = values.clone();
        sorted = Set.copyOf(sorted);
    }

    public String describe() {
        return switch (type) {
            case COMPARE -> "Compare [" + i + "] and [" + j + "]";
            case SWAP -> "Swap [" + i + "] and [" + j + "]";
            case WRITE -> "Write " + values[i] + " to [" + i + "]";
            case MARK_SORTED -> "[" + i + "] is in its final position";
            case DONE -> "Sorted";
        };
    }
}
