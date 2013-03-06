package com.ericguerra.algoviz.sort;

public final class InsertionSort implements SortAlgorithm {
    @Override public String name() { return "Insertion sort"; }

    @Override public String complexity() { return "O(n²) time, O(1) space, stable, adaptive"; }

    @Override
    public void sort(SortRecorder r) {
        for (int i = 1; i < r.length(); i++) {
            int j = i;
            while (j > 0 && r.compare(j - 1, j) > 0) {
                r.swap(j - 1, j);
                j--;
            }
        }
    }
}
