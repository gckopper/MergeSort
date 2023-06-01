package com.example;

/* Java program for Merge Sort */
// this program tracks the number of chages it makes
// to the original array. A chage is only valid if a
// number goes to a different position during the merging process
class MergeSort {
    // Merges two subarrays of arr[].
    // First subarray is arr[l..m]
    // Second subarray is arr[m+1..r]
    public static int merge(int arr[], int left, int middle, int right, int tracker) {
        // Find sizes of two subarrays to be merged
        int leftSize = middle - left + 1;
        int rightSize = right - middle;

        /* Create temp arrays */
        int L[] = new int[leftSize];
        int R[] = new int[rightSize];

        /* Copy data to temp arrays */
        for (int i = 0; i < leftSize; ++i)
            L[i] = arr[left + i];
        for (int j = 0; j < rightSize; ++j)
            R[j] = arr[middle + 1 + j];

        /* Merge the temp arrays */

        // Initial indexes of first and second subarrays
        int leftIndex = 0, rightIndex = 0;

        // Initial index of merged subarray array
        int k = left;
        while (leftIndex < leftSize && rightIndex < rightSize) {
            if (L[leftIndex] <= R[rightIndex]) {
                arr[k] = L[leftIndex];
                leftIndex++;
                if (rightIndex != 0)
                    tracker++;
            } else {
                arr[k] = R[rightIndex];
                rightIndex++;
            }
            k++;
        }

        /* Copy remaining elements of L[] if any */
        while (leftIndex < leftSize) {
            arr[k] = L[leftIndex];
            leftIndex++;
            k++;
            // if left elements are added at the end they are in a diff position
            tracker++;
        }

        /* Copy remaining elements of R[] if any */
        while (rightIndex < rightSize) {
            arr[k] = R[rightIndex];
            rightIndex++;
            k++;
            // if right elements are added at the end they are in the same position
        }
        return tracker;
    }

    // Main function that sorts arr[l..r] using
    // merge()
    public static int sort(int arr[], int left, int right, int tracker) {
        if (left < right) {
            // Find the middle point
            int middle = (left + right) / 2;

            // Sort first and second halves
            tracker = sort(arr, left, middle, tracker);
            tracker = sort(arr, middle + 1, right, tracker);

            // Merge the sorted halves
            tracker = merge(arr, left, middle, right, tracker);
        }
        return tracker;
    }

    // Convinience method
    public static int sort(int arr[]) {
        return sort(arr, 0, arr.length - 1, 0);
    }

    /* A utility function to print array of size n */
    static void printArray(int arr[]) {
        int n = arr.length;
        for (int i = 0; i < n; ++i)
            System.out.print(arr[i] + " ");
        System.out.println();
    }

    // Driver method
    public static void main(String args[]) {
        int arr[] = { 12, 11, 13, 5, 6, 7 };

        System.out.println("Given Array");
        printArray(arr);
        Integer a = 0;
        sort(arr, 0, arr.length - 1, a);
        System.out.println(a);
        System.out.println(arr);

        System.out.println("\nSorted array");
        printArray(arr);
    }
}
/* This code is contributed by Rajat Mishra */