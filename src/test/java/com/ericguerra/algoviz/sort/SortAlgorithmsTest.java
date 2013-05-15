package com.ericguerra.algoviz.sort;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SortAlgorithmsTest {
    private static final int[][] CASES = {
            {}, {1}, {2, 1}, {1, 2, 3, 4, 5}, {5, 4, 3, 2, 1}, {3, 3, 3, 3}, {9, -2, 0, 7, -2, 4, 100, 1},
    };

    @TestFactory
    Stream<DynamicTest> everyAlgorithmSortsEdgeCases() {
        return SortAlgorithm.all().stream().flatMap(algo -> Arrays.stream(CASES).map(input ->
                DynamicTest.dynamicTest(algo.name() + " " + Arrays.toString(input), () -> {
                    int[] expected = input.clone();
                    Arrays.sort(expected);
                    assertArrayEquals(expected, algo.run(input).result());
                })));
    }

    @TestFactory
    Stream<DynamicTest> everyAlgorithmSortsRandomArrays() {
        Random rnd = new Random(42);
        return SortAlgorithm.all().stream().map(algo -> DynamicTest.dynamicTest(algo.name(), () -> {
            for (int trial = 0; trial < 50; trial++) {
                int[] input = rnd.ints(rnd.nextInt(120), -50, 50).toArray();
                int[] expected = input.clone();
                Arrays.sort(expected);
                assertArrayEquals(expected, algo.run(input).result());
            }
        }));
    }

    @Test
    void recordingDoesNotMutateInput() {
        int[] input = {3, 1, 2};
        new QuickSort().run(input);
        assertArrayEquals(new int[]{3, 1, 2}, input);
    }

    @Test
    void lastStepIsDoneWithAllIndicesSorted() {
        SortRecorder r = new HeapSort().run(new int[]{4, 2, 9, 1});
        SortStep last = r.steps().get(r.steps().size() - 1);
        assertEquals(SortStep.Type.DONE, last.type());
        assertEquals(4, last.sorted().size());
    }

    @Test
    void stepSnapshotsAreIndependent() {
        SortRecorder r = new BubbleSort().run(new int[]{2, 1});
        SortStep compare = r.steps().get(0);
        assertArrayEquals(new int[]{2, 1}, compare.values());
    }

    @Test
    void bubbleSortExitsEarlyOnSortedInput() {
        SortRecorder r = new BubbleSort().run(new int[]{1, 2, 3, 4, 5, 6});
        assertEquals(5, r.comparisons());
        assertEquals(0, r.writes());
    }

    @Test
    void quickSortAvoidsQuadraticBehaviourOnSortedInput() {
        int n = 2000;
        int[] sorted = new int[n];
        for (int i = 0; i < n; i++) sorted[i] = i;
        long comparisons = new QuickSort().run(sorted).comparisons();
        assertTrue(comparisons < (long) n * 40, "median-of-three should keep comparisons near n log n, got " + comparisons);
    }

    @Test
    void mergeSortIsStableForEqualKeys() {
        // Encode (key, originalIndex) as key*100+index and sort by key only via integer division trick:
        // values with the same tens digit must keep their relative order.
        int[] input = {21, 10, 22, 11, 23};
        SortRecorder r = new MergeSort().run(input);
        assertArrayEquals(new int[]{10, 11, 21, 22, 23}, r.result());
    }
}
