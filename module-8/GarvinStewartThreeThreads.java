// Name: Garvin Stewart
// Date: 09/20/2026
// Assignment: CSD 420 Module 2 - ThreeThreads (Multithreading)
// Purpose: Run three threads that each emit 10,000 random characters
//          (letters, digits, symbols) into one shared TextArea, interleaved
//          in real time as each character is generated.

/**
 * Three worker threads, one per character type. Each thread generates one
 * random character at a time and hands it to a shared sink, so the output of
 * the three threads interleaves (e.g. 179$^fq*fg1k1k2^%1m4).
 *
 * The thread logic is kept separate from the GUI: the sink is a
 * java.util.function.Consumer, so the JavaFX TextArea and the test code can
 * both plug into the same generator code.
 */
public class GarvinStewartThreeThreads extends javafx.application.Application {

  // Required minimum characters per thread.
  public static final int CHARACTERS_PER_THREAD = 10000;

  // Character pools for each thread.
  public static final String LETTER_POOL =
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  public static final String DIGIT_POOL = "0123456789";
  public static final String SYMBOL_POOL = "!@#$%^&*";

  /**
   * Returns one random character from the given pool.
   * Uses ThreadLocalRandom so the three threads never contend on one Random.
   */
  public static char randomCharacterFrom(String pool) {
    if (pool == null || pool.isEmpty()) {
      throw new IllegalArgumentException("Character pool must not be empty");
    }
    int randomIndex =
      java.util.concurrent.ThreadLocalRandom.current().nextInt(pool.length());
    return pool.charAt(randomIndex);
  }

  /**
   * Pauses the current thread for 0-49 microseconds (chosen at random).
   * LockSupport.parkNanos is used instead of Thread.sleep because sleep only
   * has millisecond granularity, which would make 10,000 iterations far too slow.
   */
  private static void pauseBriefly() {
    long nanos = java.util.concurrent.ThreadLocalRandom.current().nextLong(50_000L);
    if (nanos > 0) {
      java.util.concurrent.locks.LockSupport.parkNanos(nanos);
    }
  }

  /**
   * Runnable that emits a fixed number of random characters from one pool.
   * Implementing Runnable (rather than extending Thread) follows the
   * Runnable interface examples in the Module 2 resources.
   */
  public static class CharacterGenerator implements Runnable {
    private final String characterPool;
    private final int numberOfCharacters;
    private final java.util.function.Consumer<Character> outputSink;

    public CharacterGenerator(String characterPool, int numberOfCharacters,
        java.util.function.Consumer<Character> outputSink) {
      this.characterPool = characterPool;
      this.numberOfCharacters = numberOfCharacters;
      this.outputSink = outputSink;
    }

    @Override
    public void run() {
      for (int emitted = 0; emitted < numberOfCharacters; emitted++) {
        // One character is sent out as soon as it is generated.
        outputSink.accept(randomCharacterFrom(characterPool));

        // Brief random pause so the three threads genuinely race each other.
        // Thread.yield() alone made them take strict turns (x9!y2@z4#), which
        // hides the concurrency; a short random sleep gives natural, uneven
        // interleaving like 179$^fq*fg1k1k2^%1m4. Runs of 0 ns are common,
        // so this adds well under a second in total.
        pauseBriefly();
      }
    }
  }

  /**
   * Starts the three threads, waits for all of them with join(), and returns.
   * Blocks the calling thread, so the GUI must call it from a background thread.
   */
  public static void runThreeThreads(int charactersPerThread,
      java.util.function.Consumer<Character> outputSink)
      throws InterruptedException {
    Thread letterThread = new Thread(
      new CharacterGenerator(LETTER_POOL, charactersPerThread, outputSink),
      "letter-thread");
    Thread digitThread = new Thread(
      new CharacterGenerator(DIGIT_POOL, charactersPerThread, outputSink),
      "digit-thread");
    Thread symbolThread = new Thread(
      new CharacterGenerator(SYMBOL_POOL, charactersPerThread, outputSink),
      "symbol-thread");

    letterThread.start();
    digitThread.start();
    symbolThread.start();

    // join() makes the caller wait until each thread has finished.
    letterThread.join();
    digitThread.join();
    symbolThread.join();
  }

  // ---------------------------------------------------------------- GUI ---

  @Override
  public void start(javafx.stage.Stage primaryStage) {
    javafx.scene.control.TextArea outputArea = new javafx.scene.control.TextArea();
    outputArea.setWrapText(true);
    outputArea.setEditable(false);

    javafx.scene.control.Label statusLabel =
      new javafx.scene.control.Label("Running three threads...");

    javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane();
    root.setCenter(outputArea);
    root.setBottom(statusLabel);

    // Worker threads must not touch the TextArea directly (JavaFX allows that
    // only on the Application Thread) and calling Platform.runLater 30,000 times
    // would flood the event queue. Instead the workers drop characters in this
    // queue and a timer on the FX thread drains it in batches.
    java.util.concurrent.ConcurrentLinkedQueue<Character> pendingCharacters =
      new java.util.concurrent.ConcurrentLinkedQueue<>();
    java.util.concurrent.atomic.AtomicBoolean workersFinished =
      new java.util.concurrent.atomic.AtomicBoolean(false);
    int expectedTotal = CHARACTERS_PER_THREAD * 3;
    int[] displayedCount = {0};

    javafx.animation.AnimationTimer drainTimer = new javafx.animation.AnimationTimer() {
      @Override
      public void handle(long now) {
        StringBuilder batch = new StringBuilder();
        Character next;
        while ((next = pendingCharacters.poll()) != null) {
          batch.append(next.charValue());
        }
        if (batch.length() > 0) {
          outputArea.appendText(batch.toString());
          displayedCount[0] += batch.length();
        }
        // Stop only when the workers are done AND the queue is fully drained.
        if (workersFinished.get() && pendingCharacters.isEmpty()) {
          statusLabel.setText("Done: " + displayedCount[0] + " of "
            + expectedTotal + " characters displayed");
          stop();
        }
      }
    };

    // Coordinator thread: runs the three workers and waits on them with join(),
    // so the GUI thread is never blocked.
    Thread coordinatorThread = new Thread(() -> {
      try {
        runThreeThreads(CHARACTERS_PER_THREAD, pendingCharacters::add);
      } catch (InterruptedException interrupted) {
        Thread.currentThread().interrupt();
      } finally {
        workersFinished.set(true);
      }
    }, "coordinator-thread");
    coordinatorThread.setDaemon(true);

    primaryStage.setTitle("Garvin Stewart - Three Threads");
    primaryStage.setScene(new javafx.scene.Scene(root, 700, 450));
    primaryStage.show();

    drainTimer.start();
    coordinatorThread.start();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
