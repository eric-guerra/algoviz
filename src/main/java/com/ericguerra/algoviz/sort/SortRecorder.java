package com.ericguerra.algoviz.sort;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Array wrapper that algorithms operate on. Every access that matters for
 * visualization is recorded as a {@link SortStep}, and counters are kept so the
 * UI can show comparisons and writes side by side.
 */
public final class SortRecorder {
    private final int[] a;
    private final List<SortStep> steps = new ArrayList<>();
    private final Set<Integer> sorted = new HashSet<>();
    private long comparisons;
    private long writes;

    public SortRecorder(int[] input) {
        this.a = input.clone();
    }

    public int length() {
        return a.length;
    }

    public int get(int i) {
        return a[i];
    }

    /** Returns a negative, zero or positive value like {@link Integer#compare}. */
    public int compare(int i, int j) {
        comparisons++;
        steps.add(new SortStep(SortStep.Type.COMPARE, i, j, a, sorted));
        return Integer.compare(a[i], a[j]);
    }

    /** Compare an element against an out-of-array value (e.g. a pivot or key). */
    public int compareValue(int i, int value) {
        comparisons++;
        steps.add(new SortStep(SortStep.Type.COMPARE, i, i, a, sorted));
        return Integer.compare(a[i], value);
    }

    public void swap(int i, int j) {
        if (i == j) return;
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
        writes += 2;
        steps.add(new SortStep(SortStep.Type.SWAP, i, j, a, sorted));
    }

    public void write(int i, int value) {
        a[i] = value;
        writes++;
        steps.add(new SortStep(SortStep.Type.WRITE, i, i, a, sorted));
    }

    public void markSorted(int i) {
        if (sorted.add(i)) {
            steps.add(new SortStep(SortStep.Type.MARK_SORTED, i, i, a, sorted));
        }
    }

    public void finish() {
        for (int i = 0; i < a.length; i++) sorted.add(i);
        steps.add(new SortStep(SortStep.Type.DONE, -1, -1, a, sorted));
    }

    public int[] result() {
        return a.clone();
    }

    public List<SortStep> steps() {
        return List.copyOf(steps);
    }

    public long comparisons() {
        return comparisons;
    }

    public long writes() {
        return writes;
    }
}
