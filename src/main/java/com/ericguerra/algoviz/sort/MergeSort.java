package com.ericguerra.algoviz.sort;

public final class MergeSort implements SortAlgorithm {
    @Override public String name() { return "Merge sort"; }

    @Override public String complexity() { return "O(n log n) time, O(n) space, stable"; }

    @Override
    public void sort(SortRecorder r) {
        if (r.length() > 1) sort(r, 0, r.length() - 1);
    }

    private void sort(SortRecorder r, int lo, int hi) {
        if (lo >= hi) return;
        int mid = (lo + hi) >>> 1;
        sort(r, lo, mid);
        sort(r, mid + 1, hi);
        merge(r, lo, mid, hi);
    }

    private void merge(SortRecorder r, int lo, int mid, int hi) {
        int[] left = new int[mid - lo + 1];
        for (int k = 0; k < left.length; k++) left[k] = r.get(lo + k);

        int i = 0;          // index into left copy
        int j = mid + 1;    // index into right half, still in place
        int out = lo;
        while (i < left.length && j <= hi) {
            // <= keeps equal elements in original order (stability)
            if (r.compareValue(j, left[i]) >= 0) {
                r.write(out++, left[i++]);
            } else {
                r.write(out++, r.get(j++));
            }
        }
        while (i < left.length) r.write(out++, left[i++]);
    }
}
