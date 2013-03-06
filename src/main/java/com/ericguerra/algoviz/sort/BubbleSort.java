package com.ericguerra.algoviz.sort;

public final class BubbleSort implements SortAlgorithm {
    @Override public String name() { return "Bubble sort"; }

    @Override public String complexity() { return "O(n²) time, O(1) space, stable"; }

    @Override
    public void sort(SortRecorder r) {
        int n = r.length();
        for (int end = n - 1; end > 0; end--) {
            boolean swapped = false;
            for (int i = 0; i < end; i++) {
                if (r.compare(i, i + 1) > 0) {
                    r.swap(i, i + 1);
                    swapped = true;
                }
            }
            r.markSorted(end);
            if (!swapped) break; // already sorted: early exit gives O(n) best case
        }
    }
}
