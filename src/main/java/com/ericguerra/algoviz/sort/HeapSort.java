package com.ericguerra.algoviz.sort;

public final class HeapSort implements SortAlgorithm {
    @Override public String name() { return "Heap sort"; }

    @Override public String complexity() { return "O(n log n) time, O(1) space, not stable"; }

    @Override
    public void sort(SortRecorder r) {
        int n = r.length();
        for (int i = n / 2 - 1; i >= 0; i--) siftDown(r, i, n);
        for (int end = n - 1; end > 0; end--) {
            r.swap(0, end);
            r.markSorted(end);
            siftDown(r, 0, end);
        }
    }

    private void siftDown(SortRecorder r, int root, int size) {
        while (true) {
            int largest = root;
            int left = 2 * root + 1;
            int right = left + 1;
            if (left < size && r.compare(left, largest) > 0) largest = left;
            if (right < size && r.compare(right, largest) > 0) largest = right;
            if (largest == root) return;
            r.swap(root, largest);
            root = largest;
        }
    }
}
