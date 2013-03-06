package com.ericguerra.algoviz.sort;

/** Lomuto partition with median-of-three pivot selection to avoid O(n²) on sorted input. */
public final class QuickSort implements SortAlgorithm {
    @Override public String name() { return "Quick sort"; }

    @Override public String complexity() { return "O(n log n) avg, O(n²) worst, O(log n) space"; }

    @Override
    public void sort(SortRecorder r) {
        sort(r, 0, r.length() - 1);
    }

    private void sort(SortRecorder r, int lo, int hi) {
        while (lo < hi) {
            int p = partition(r, lo, hi);
            r.markSorted(p);
            // Recurse into the smaller side, loop on the larger: bounds stack depth to O(log n).
            if (p - lo < hi - p) {
                sort(r, lo, p - 1);
                lo = p + 1;
            } else {
                sort(r, p + 1, hi);
                hi = p - 1;
            }
        }
        if (lo == hi) r.markSorted(lo);
    }

    private int partition(SortRecorder r, int lo, int hi) {
        int mid = (lo + hi) >>> 1;
        if (r.compare(mid, lo) < 0) r.swap(mid, lo);
        if (r.compare(hi, lo) < 0) r.swap(hi, lo);
        if (r.compare(mid, hi) < 0) r.swap(mid, hi); // median now at hi
        int pivot = r.get(hi);

        int store = lo;
        for (int i = lo; i < hi; i++) {
            if (r.compareValue(i, pivot) < 0) {
                r.swap(i, store++);
            }
        }
        r.swap(store, hi);
        return store;
    }
}
