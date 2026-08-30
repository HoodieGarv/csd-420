/*
 * Garvin Stewart
 * Aug 30, 2026
 * CSD 420 - Module 4.2 Programming Assignment
 * LinkedList Traversal Timing Test
 *
 * Purpose: Compares the time required to traverse a LinkedList using an
 * Iterator versus using the get(index) method, for lists of 50,000 and
 * 500,000 Integer elements, and verifies both traversal approaches with
 * a small-scale correctness test.
 */
public class LinkedListTraversalTest {

  public static void main(String[] args) {

    // Run correctness tests first so we trust the traversal logic before
    // trusting the timing numbers below
    runCorrectnessTests();

    System.out.println();
    System.out.println("===== Timing Results =====");
    System.out.println();

    // Test with 50,000 elements
    runTimingTest(50_000);

    System.out.println();

    // Test with 500,000 elements
    // NOTE: the get(index) pass on 500,000 elements is intentionally slow
    //       -- see the comment on traverseWithGet() below for why.
    runTimingTest(500_000);
  }

  // ---------------------------------------------------------------
  // Builds a LinkedList of the requested size, filled with sequential
  // integers 0..size-1. Sequential values (rather than random) make it
  // trivial to verify correctness, since the expected sum is known.
  // ---------------------------------------------------------------
  private static java.util.LinkedList<Integer> buildList(int size) {

    java.util.LinkedList<Integer> list = new java.util.LinkedList<>();

    for (int i = 0; i < size; i++) {
      list.add(i);
    }

    return list;
  }

  // ---------------------------------------------------------------
  // Traverses the list using an Iterator and returns the sum of all
  // elements (used for correctness checking) and the elapsed time in
  // nanoseconds. Iterator traversal is O(n): each next() call moves one
  // node forward from wherever the cursor currently is.
  // ---------------------------------------------------------------
  private static long[] traverseWithIterator(java.util.LinkedList<Integer> list) {

    long start = System.nanoTime();
    long sum = 0;

    java.util.Iterator<Integer> iterator = list.iterator();

    while (iterator.hasNext()) {
      sum += iterator.next();
    }

    long elapsed = System.nanoTime() - start;

    return new long[] { sum, elapsed };
  }

  // ---------------------------------------------------------------
  // Traverses the list using get(index) in a for loop and returns the
  // sum of all elements and the elapsed time in nanoseconds.
  //
  // Each call to LinkedList.get(index) walks the list node-by-node from
  // whichever end (head or tail) is closer to the requested index, so a
  // single call costs O(n) in the worst case. Calling get() once per
  // element in a loop therefore costs O(n) * O(n) = O(n^2) overall --
  // this is the behavior this test is designed to expose.
  // ---------------------------------------------------------------
  private static long[] traverseWithGet(java.util.LinkedList<Integer> list) {

    long start = System.nanoTime();
    long sum = 0;
    int size = list.size();

    for (int i = 0; i < size; i++) {
      sum += list.get(i);
    }

    long elapsed = System.nanoTime() - start;

    return new long[] { sum, elapsed };
  }

  // ---------------------------------------------------------------
  // Builds a list of the given size, times both traversal approaches,
  // and prints a formatted comparison.
  // ---------------------------------------------------------------
  private static void runTimingTest(int size) {

    System.out.println("--- List size: " + size + " ---");

    java.util.LinkedList<Integer> list = buildList(size);

    long[] iteratorResult = traverseWithIterator(list);
    long[] getResult = traverseWithGet(list);

    double iteratorMillis = iteratorResult[1] / 1_000_000.0;
    double getMillis = getResult[1] / 1_000_000.0;

    System.out.printf("Iterator traversal:   %,10.2f ms  (sum = %d)%n",
        iteratorMillis, iteratorResult[0]);
    System.out.printf("get(index) traversal: %,10.2f ms  (sum = %d)%n",
        getMillis, getResult[0]);
    System.out.printf("get(index) was %.1fx slower than the Iterator%n",
        getMillis / iteratorMillis);
  }

  // =================================================================
  // TEST CODE -- verifies both traversal methods work correctly before
  // the timing numbers above are trusted.
  // =================================================================
  private static void runCorrectnessTests() {

    System.out.println("===== Correctness Tests =====");
    int passed = 0;
    int failed = 0;

    // Test 1: empty list should sum to 0 with both methods
    java.util.LinkedList<Integer> empty = buildList(0);
    if (traverseWithIterator(empty)[0] == 0 && traverseWithGet(empty)[0] == 0) {
      System.out.println("PASS: empty list sums to 0");
      passed++;
    } else {
      System.out.println("FAIL: empty list did not sum to 0");
      failed++;
    }

    // Test 2: known small list, sum should equal 0+1+2+...+(n-1)
    int n = 1000;
    long expectedSum = (long) n * (n - 1) / 2;
    java.util.LinkedList<Integer> small = buildList(n);

    long iteratorSum = traverseWithIterator(small)[0];
    long getSum = traverseWithGet(small)[0];

    if (iteratorSum == expectedSum) {
      System.out.println("PASS: iterator sum correct for n=" + n);
      passed++;
    } else {
      System.out.println("FAIL: iterator sum expected " + expectedSum + " got " + iteratorSum);
      failed++;
    }

    if (getSum == expectedSum) {
      System.out.println("PASS: get(index) sum correct for n=" + n);
      passed++;
    } else {
      System.out.println("FAIL: get(index) sum expected " + expectedSum + " got " + getSum);
      failed++;
    }

    // Test 3: both methods should agree with each other on the same list
    if (iteratorSum == getSum) {
      System.out.println("PASS: iterator and get(index) sums match");
      passed++;
    } else {
      System.out.println("FAIL: iterator and get(index) sums do not match");
      failed++;
    }

    System.out.println(passed + " passed, " + failed + " failed");
  }
}

/*
 * ===================================================================
 * RESULTS AND DISCUSSION
 * ===================================================================
 * Measured on this machine (OpenJDK, single run, values in milliseconds):
 *
 *   Size = 50,000
 *     Iterator traversal:    ~4.5 ms
 *     get(index) traversal:  ~957 ms      (~211x slower than the iterator)
 *
 *   Size = 500,000  (10x the elements)
 *     Iterator traversal:    ~7.9 ms      (~1.8x slower than at 50,000)
 *     get(index) traversal:  ~156,031 ms  (~2.6 minutes; ~19,651x slower
 *                                          than the iterator, and ~163x
 *                                          slower than get(index) at 50,000)
 *
 * Why the Iterator stays fast:
 *   A LinkedList is a doubly linked list, so it has no notion of a
 *   backing array or random access. Iterator.next() simply follows the
 *   "next" reference from wherever the cursor currently sits, which is
 *   O(1) per call. Traversing the whole list is n calls to next(), so
 *   the total cost is O(n) -- it grows in a straight line with the
 *   list size, which is why 10x the elements only cost about 1.8x the
 *   time (JVM warm-up/JIT effects account for the rest of the gap;
 *   both numbers are close to the noise floor).
 *
 * Why get(index) blows up:
 *   LinkedList.get(index) has no shortcut to the middle of the list --
 *   it walks node-by-node from the head (or from the tail, whichever is
 *   closer to the requested index) until it reaches the target, which
 *   is O(n) per call. Calling get(i) once for every i from 0 to
 *   size-1 means doing that O(n) walk n times, for a total cost of
 *   O(n) * O(n) = O(n^2). Quadratic growth means multiplying the input
 *   size by 10 should multiply the runtime by roughly 10^2 = 100; the
 *   observed ~163x is in that same ballpark (the extra overhead comes
 *   from cache misses and GC pressure once the list and its Integer
 *   objects no longer fit comfortably in cache at 500,000 elements).
 *
 * Bottom line:
 *   For an ArrayList, get(index) is O(1) because the backing array
 *   supports direct indexing, so looping with get() is a reasonable
 *   choice. For a LinkedList, get(index) is O(n), so looping with it is
 *   effectively O(n^2) and becomes unusable at scale -- the 500,000
 *   case above took nearly three minutes, versus 8 milliseconds for the
 *   Iterator. The practical takeaway: always traverse a LinkedList with
 *   an Iterator (or a for-each loop, which uses one under the hood),
 *   and reserve get(index) for ArrayList or for one-off lookups near
 *   the ends of a LinkedList.
 * ===================================================================
 */
