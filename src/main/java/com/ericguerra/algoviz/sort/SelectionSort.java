package com.ericguerra.algoviz.sort;

public final class SelectionSort implements SortAlgorithm {
    @Override public String name() { return "Selection sort"; }

    @Override public String complexity() { return "O(n²) time, O(1) space, not stable"; }

    @Override
    public void sort(SortRecorder r) {
        int n = r.length();
        for (int start = 0; start < n - 1; start++) {
            int min = start;
            for (int i = start + 1; i < n; i++) {
                if (r.compare(i, min) < 0) min = i;
            }
            r.swap(start, min);
            r.markSorted(start);
        }
    }
}
