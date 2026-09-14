/*
 * Name: Garvin Stewart
 * Date: Sept 13, 2026
 * Assignment: Module 6 Programming Assignment - Generic Bubble Sort
 *             (Comparable and Comparator versions)
 *
 * Purpose: Provides two generic bubble sort methods -- one that orders
 * elements using the Comparable interface's natural ordering, and one
 * that orders elements using a caller-supplied Comparator -- so the same
 * sort algorithm can be reused for any object type without rewriting it.
 */
public class GenericBubbleSort {

  // Sorts an array of Comparable objects into ascending order.
  // T extends Comparable<T> so any two elements in the array can be
  // compared directly with compareTo() -- this is what lets the method
  // work for Integer[], String[], or any custom class that implements
  // Comparable, instead of only int[].
  public static <T extends Comparable<T>> void bubbleSortComparable(T[] list) {

    int n = list.length;
    boolean swapped;

    // Each outer pass "bubbles" the largest remaining element into its
    // final position at the end of the unsorted region, so the region
    // we still need to check shrinks by one element each pass.
    for (int pass = 0; pass < n - 1; pass++) {

      swapped = false;

      // Walk the unsorted region and swap any adjacent pair that is
      // out of order.
      for (int j = 0; j < n - 1 - pass; j++) {

        // compareTo() > 0 means list[j] is "greater than" list[j + 1]
        // under this type's natural ordering, so they are out of order.
        if (list[j].compareTo(list[j + 1]) > 0) {

          T temp = list[j];
          list[j] = list[j + 1];
          list[j + 1] = temp;
          swapped = true;
        }
      }

      // If a full pass made no swaps, the array is already sorted --
      // stop early instead of running the remaining passes.
      if (!swapped) {
        break;
      }
    }
  }

  // Sorts an array of any object type into the order defined by the
  // supplied Comparator. This version does not require T to implement
  // Comparable at all -- the ordering logic lives entirely in the
  // Comparator, so the same list could be sorted a different way just
  // by passing a different comparator.
  public static <T> void bubbleSortComparator(T[] list, java.util.Comparator<T> comparator) {

    int n = list.length;
    boolean swapped;

    for (int pass = 0; pass < n - 1; pass++) {

      swapped = false;

      for (int j = 0; j < n - 1 - pass; j++) {

        // compare() > 0 means list[j] should come after list[j + 1]
        // under the ordering the comparator defines.
        if (comparator.compare(list[j], list[j + 1]) > 0) {

          T temp = list[j];
          list[j] = list[j + 1];
          list[j + 1] = temp;
          swapped = true;
        }
      }

      if (!swapped) {
        break;
      }
    }
  }

  // Prints array contents on one line, in the same bracketed format used
  // in Professor Payne's Bubble_Sort.java example, generalized here to
  // accept any object type.
  public static <T> void printArray(T[] arrayParam) {

    System.out.print("array = {");

    for (T e : arrayParam) {
      System.out.print(" [" + e + "] ");
    }

    System.out.println("}");
  }
}
