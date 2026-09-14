/*
 * Name: Garvin Stewart
 * Date: Sept 13, 2026
 * Assignment: Module 6 Programming Assignment - Test code for
 *             GenericBubbleSort
 *
 * Purpose: Exercises bubbleSortComparable() and bubbleSortComparator()
 * against several array types and edge cases, then checks each result
 * against what a correctly sorted array should look like and reports
 * PASSED/FAILED for each case.
 */
public class TestGenericBubbleSort {

  public static void main(String[] args) {

    int passCount = 0;
    int totalCount = 0;

    // ---- Test 1: Comparable, Integer[] ----------------------------
    Integer[] numbers = {5, 3, 8, 1, 9, 2, 7};
    System.out.println("Test 1: bubbleSortComparable() on Integer[]");
    GenericBubbleSort.printArray(numbers);
    GenericBubbleSort.bubbleSortComparable(numbers);
    GenericBubbleSort.printArray(numbers);
    totalCount++;
    if (isSortedAscending(numbers)) {
      passCount++;
      report("Test 1", true);
    } else {
      report("Test 1", false);
    }
    System.out.println();

    // ---- Test 2: Comparable, String[] ------------------------------
    String[] words = {"banana", "apple", "cherry", "date", "apple"};
    System.out.println("Test 2: bubbleSortComparable() on String[]");
    GenericBubbleSort.printArray(words);
    GenericBubbleSort.bubbleSortComparable(words);
    GenericBubbleSort.printArray(words);
    totalCount++;
    if (isSortedAscending(words)) {
      passCount++;
      report("Test 2", true);
    } else {
      report("Test 2", false);
    }
    System.out.println();

    // ---- Test 3: Comparable, custom class (Movie, ordered by rating) --
    Movie[] moviesByRating = {
      new Movie("Aftershock", 6.2),
      new Movie("Blue Harbor", 8.9),
      new Movie("Coastline", 7.1),
      new Movie("Driftwood", 5.4)
    };
    System.out.println("Test 3: bubbleSortComparable() on Movie[] (natural order = rating)");
    GenericBubbleSort.printArray(moviesByRating);
    GenericBubbleSort.bubbleSortComparable(moviesByRating);
    GenericBubbleSort.printArray(moviesByRating);
    totalCount++;
    if (isSortedAscending(moviesByRating)) {
      passCount++;
      report("Test 3", true);
    } else {
      report("Test 3", false);
    }
    System.out.println();

    // ---- Test 4: Comparator, custom class (Movie, ordered by title) --
    Movie[] moviesByTitle = {
      new Movie("Driftwood", 5.4),
      new Movie("Blue Harbor", 8.9),
      new Movie("Aftershock", 6.2),
      new Movie("Coastline", 7.1)
    };

    // Anonymous class supplying a different ordering than Movie's own
    // compareTo() -- this is the whole point of the Comparator version:
    // the same bubbleSortComparator() method can sort by any rule
    // without Movie needing to change.
    java.util.Comparator<Movie> byTitle = new java.util.Comparator<Movie>() {
      @Override
      public int compare(Movie m1, Movie m2) {
        return m1.getTitle().compareTo(m2.getTitle());
      }
    };

    System.out.println("Test 4: bubbleSortComparator() on Movie[] (custom order = title)");
    GenericBubbleSort.printArray(moviesByTitle);
    GenericBubbleSort.bubbleSortComparator(moviesByTitle, byTitle);
    GenericBubbleSort.printArray(moviesByTitle);
    totalCount++;
    if (isSortedAscending(moviesByTitle, byTitle)) {
      passCount++;
      report("Test 4", true);
    } else {
      report("Test 4", false);
    }
    System.out.println();

    // ---- Test 5: edge cases -- already sorted, single element, empty --
    Integer[] alreadySorted = {1, 2, 3, 4, 5};
    Integer[] oneElement = {42};
    Integer[] empty = {};

    GenericBubbleSort.bubbleSortComparable(alreadySorted);
    GenericBubbleSort.bubbleSortComparable(oneElement);
    GenericBubbleSort.bubbleSortComparable(empty);

    boolean edgeCasesPassed = isSortedAscending(alreadySorted)
        && isSortedAscending(oneElement)
        && isSortedAscending(empty);

    System.out.println("Test 5: edge cases (already sorted, single element, empty array)");
    totalCount++;
    if (edgeCasesPassed) {
      passCount++;
      report("Test 5", true);
    } else {
      report("Test 5", false);
    }
    System.out.println();

    System.out.println(passCount + " / " + totalCount + " tests passed.");
  }

  // Confirms a Comparable[] is in ascending order by checking every
  // adjacent pair -- used to verify bubbleSortComparable()'s output.
  private static <T extends Comparable<T>> boolean isSortedAscending(T[] list) {

    for (int i = 0; i < list.length - 1; i++) {
      if (list[i].compareTo(list[i + 1]) > 0) {
        return false;
      }
    }
    return true;
  }

  // Same check as above, but using a Comparator instead of natural
  // ordering -- used to verify bubbleSortComparator()'s output.
  private static <T> boolean isSortedAscending(T[] list, java.util.Comparator<T> comparator) {

    for (int i = 0; i < list.length - 1; i++) {
      if (comparator.compare(list[i], list[i + 1]) > 0) {
        return false;
      }
    }
    return true;
  }

  // Prints a PASSED/FAILED line for a given test name.
  private static void report(String testName, boolean passed) {
    System.out.println(testName + ": " + (passed ? "PASSED" : "FAILED"));
  }
}
