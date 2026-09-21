// Name: Garvin Stewart
// Date: 09/20/2026
// Assignment: CSD 420 Module 2 - ThreeThreads (Multithreading) - Test Code
// Purpose: Verify counts, character sets, and interleaving for
//          GarvinStewartThreeThreads without needing a GUI.

/**
 * Self-contained test runner. Run with: java GarvinStewartThreeThreadsTest
 * Exits with status 1 if any check fails.
 */
public class GarvinStewartThreeThreadsTest {

  private static int passedChecks = 0;
  private static int failedChecks = 0;

  private static void check(String description, boolean condition) {
    if (condition) {
      passedChecks++;
      System.out.println("  PASS: " + description);
    } else {
      failedChecks++;
      System.out.println("  FAIL: " + description);
    }
  }

  /** Runs the three threads and returns everything they emitted, in arrival order. */
  private static String captureOutput(int charactersPerThread)
      throws InterruptedException {
    // Synchronized so concurrent appends from three threads cannot corrupt it.
    StringBuffer collected = new StringBuffer();
    GarvinStewartThreeThreads.runThreeThreads(charactersPerThread, collected::append);
    return collected.toString();
  }

  private static int countIn(String text, String pool) {
    int count = 0;
    for (int i = 0; i < text.length(); i++) {
      if (pool.indexOf(text.charAt(i)) >= 0) {
        count++;
      }
    }
    return count;
  }

  public static void main(String[] args) throws InterruptedException {
    int required = GarvinStewartThreeThreads.CHARACTERS_PER_THREAD;

    System.out.println("Test 1: randomCharacterFrom stays inside its pool");
    boolean allInPool = true;
    for (int i = 0; i < 5000; i++) {
      allInPool &= GarvinStewartThreeThreads.LETTER_POOL.indexOf(
        GarvinStewartThreeThreads.randomCharacterFrom(
          GarvinStewartThreeThreads.LETTER_POOL)) >= 0;
      allInPool &= GarvinStewartThreeThreads.DIGIT_POOL.indexOf(
        GarvinStewartThreeThreads.randomCharacterFrom(
          GarvinStewartThreeThreads.DIGIT_POOL)) >= 0;
      allInPool &= GarvinStewartThreeThreads.SYMBOL_POOL.indexOf(
        GarvinStewartThreeThreads.randomCharacterFrom(
          GarvinStewartThreeThreads.SYMBOL_POOL)) >= 0;
    }
    check("5,000 draws from each pool all belong to that pool", allInPool);

    System.out.println("Test 2: randomCharacterFrom covers the whole pool");
    java.util.Set<Character> seenDigits = new java.util.HashSet<>();
    for (int i = 0; i < 2000; i++) {
      seenDigits.add(GarvinStewartThreeThreads.randomCharacterFrom(
        GarvinStewartThreeThreads.DIGIT_POOL));
    }
    check("all 10 digits appear within 2,000 draws", seenDigits.size() == 10);

    System.out.println("Test 3: bad input is rejected");
    boolean emptyRejected = false;
    try {
      GarvinStewartThreeThreads.randomCharacterFrom("");
    } catch (IllegalArgumentException expected) {
      emptyRejected = true;
    }
    check("empty pool throws IllegalArgumentException", emptyRejected);

    boolean nullRejected = false;
    try {
      GarvinStewartThreeThreads.randomCharacterFrom(null);
    } catch (IllegalArgumentException expected) {
      nullRejected = true;
    }
    check("null pool throws IllegalArgumentException", nullRejected);

    System.out.println("Test 4: full run produces exact counts");
    String output = captureOutput(required);
    int letters = countIn(output, GarvinStewartThreeThreads.LETTER_POOL);
    int digits = countIn(output, GarvinStewartThreeThreads.DIGIT_POOL);
    int symbols = countIn(output, GarvinStewartThreeThreads.SYMBOL_POOL);
    System.out.println("  letters=" + letters + " digits=" + digits
      + " symbols=" + symbols + " total=" + output.length());
    check("exactly " + required + " letters", letters == required);
    check("exactly " + required + " digits", digits == required);
    check("exactly " + required + " symbols", symbols == required);
    check("total length is " + (required * 3) + " (no lost or extra characters)",
      output.length() == required * 3);
    check("no character outside the three pools",
      letters + digits + symbols == output.length());

    System.out.println("Test 5: output is interleaved, not grouped or in lockstep");
    // Grouped output (threads run one after another) has at most 2 type switches.
    // Lockstep output (strict letter/digit/symbol rotation) has almost no repeats
    // and almost every 3-character window using all three types. Real concurrency
    // sits between the two: many switches, but also same-type repeats and
    // windows that do NOT contain all three types.
    int typeSwitches = 0;
    int sameTypeRepeats = 0;
    int strictRotationWindows = 0;
    for (int i = 1; i < output.length(); i++) {
      if (typeOf(output.charAt(i)) != typeOf(output.charAt(i - 1))) {
        typeSwitches++;
      } else {
        sameTypeRepeats++;
      }
      if (i >= 2) {
        int a = typeOf(output.charAt(i - 2));
        int b = typeOf(output.charAt(i - 1));
        int c = typeOf(output.charAt(i));
        if (a != b && b != c && a != c) {
          strictRotationWindows++;
        }
      }
    }
    double rotationShare = strictRotationWindows / (double) (output.length() - 2);
    System.out.println("  type switches: " + typeSwitches
      + ", same-type repeats: " + sameTypeRepeats
      + ", strict-rotation share: " + String.format("%.1f%%", rotationShare * 100));
    check("more than 100 switches between character types (not grouped)",
      typeSwitches > 100);
    check("at least 100 same-type repeats (not perfect alternation)",
      sameTypeRepeats >= 100);
    check("strict-rotation share below 80% (not lockstep)", rotationShare < 0.80);

    System.out.println("Test 6: threads terminate and all three run");
    java.util.Set<String> threadNames =
      java.util.Collections.synchronizedSet(new java.util.HashSet<>());
    GarvinStewartThreeThreads.runThreeThreads(50,
      c -> threadNames.add(Thread.currentThread().getName()));
    check("emissions came from letter-, digit-, and symbol-thread",
      threadNames.contains("letter-thread")
        && threadNames.contains("digit-thread")
        && threadNames.contains("symbol-thread"));
    boolean workersGone = true;
    for (Thread t : Thread.getAllStackTraces().keySet()) {
      if (t.getName().endsWith("-thread") && t.isAlive()) {
        workersGone = false;
      }
    }
    check("no worker thread left running after join()", workersGone);

    System.out.println("Test 7: sample of interleaved output");
    System.out.println("  " + output.substring(0, 60));

    System.out.println();
    System.out.println("Results: " + passedChecks + " passed, "
      + failedChecks + " failed");
    if (failedChecks > 0) {
      System.exit(1);
    }
  }

  /** 0 = letter, 1 = digit, 2 = symbol. */
  private static int typeOf(char c) {
    if (GarvinStewartThreeThreads.LETTER_POOL.indexOf(c) >= 0) {
      return 0;
    }
    return GarvinStewartThreeThreads.DIGIT_POOL.indexOf(c) >= 0 ? 1 : 2;
  }
}
