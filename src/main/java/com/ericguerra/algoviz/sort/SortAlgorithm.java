package com.ericguerra.algoviz.sort;

import java.util.List;

public interface SortAlgorithm {
    String name();

    String complexity();

    void sort(SortRecorder r);

    default SortRecorder run(int[] input) {
        SortRecorder r = new SortRecorder(input);
        sort(r);
        r.finish();
        return r;
    }

    static List<SortAlgorithm> all() {
        return List.of(new BubbleSort(), new SelectionSort(), new InsertionSort(),
                new MergeSort(), new QuickSort(), new HeapSort());
    }
}
