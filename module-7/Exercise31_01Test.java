// Name: Garvin Stewart
// Date: 09/20/2026
// Assignment: CSD 420 Module 7 Programming Assignment (test code)
// Purpose: Verify the CSS class/ID styling and layout of Exercise31_01 without a visible window.

public class Exercise31_01Test {
  private static int checksRun = 0;
  private static int checksFailed = 0;

  // Records one pass/fail check and prints the result
  private static void check(String description, Object expected, Object actual) {
    checksRun++;
    boolean passed = java.util.Objects.equals(expected, actual);
    if (!passed) {
      checksFailed++;
    }
    System.out.println((passed ? "PASS  " : "FAIL  ") + description
        + (passed ? "" : "  (expected " + expected + " but was " + actual + ")"));
  }

  // Same as check() but for numbers that only need to match within a small tolerance
  private static void checkClose(String description, double expected, double actual, double tolerance) {
    checksRun++;
    boolean passed = Math.abs(expected - actual) <= tolerance;
    if (!passed) {
      checksFailed++;
    }
    System.out.println((passed ? "PASS  " : "FAIL  ") + description
        + (passed ? "" : "  (expected " + expected + " +/- " + tolerance + " but was " + actual + ")"));
  }

  // Collects every Circle in the scene graph, then sorts them left to right
  private static java.util.List<javafx.scene.shape.Circle> circlesLeftToRight(javafx.scene.Parent root) {
    java.util.List<javafx.scene.shape.Circle> circles = new java.util.ArrayList<>();
    collectCircles(root, circles);
    circles.sort(java.util.Comparator.comparingDouble(
        circle -> circle.localToScene(circle.getBoundsInLocal()).getMinX()));
    return circles;
  }

  private static void collectCircles(javafx.scene.Node node, java.util.List<javafx.scene.shape.Circle> found) {
    if (node instanceof javafx.scene.shape.Circle) {
      found.add((javafx.scene.shape.Circle) node);
    }
    if (node instanceof javafx.scene.Parent) {
      for (javafx.scene.Node child : ((javafx.scene.Parent) node).getChildrenUnmodifiable()) {
        collectCircles(child, found);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    // Start the JavaFX toolkit so CSS can be resolved (no window is shown)
    java.util.concurrent.CountDownLatch toolkitReady = new java.util.concurrent.CountDownLatch(1);
    javafx.application.Platform.startup(toolkitReady::countDown);
    toolkitReady.await();

    // Run all checks on the JavaFX Application Thread
    java.util.concurrent.CountDownLatch finished = new java.util.concurrent.CountDownLatch(1);
    javafx.application.Platform.runLater(() -> {
      try {
        runChecks();
      } catch (Throwable problem) {
        checksFailed++;
        System.out.println("FAIL  unexpected exception: " + problem);
        problem.printStackTrace(System.out);
      } finally {
        finished.countDown();
      }
    });
    finished.await();

    System.out.println();
    System.out.println(checksRun + " checks run, " + checksFailed + " failed");
    javafx.application.Platform.exit();
    System.exit(checksFailed == 0 ? 0 : 1);
  }

  private static void runChecks() {
    // Section 1: the style sheet file can be found
    java.net.URL cssUrl = Exercise31_01Test.class.getResource("/" + Exercise31_01.STYLESHEET_NAME);
    check("mystyle.css can be found", true, cssUrl != null);

    // Build the real layout, attach the real style sheet, and size it like the window
    javafx.scene.layout.HBox root = Exercise31_01.buildLayout();
    javafx.scene.Scene scene = new javafx.scene.Scene(root,
        Exercise31_01.SCENE_WIDTH, Exercise31_01.SCENE_HEIGHT);
    scene.getStylesheets().add(Exercise31_01.STYLESHEET_NAME);
    root.resize(Exercise31_01.SCENE_WIDTH, Exercise31_01.SCENE_HEIGHT);
    root.applyCss(); // Resolve CSS without needing a visible window
    root.layout();

    java.util.List<javafx.scene.shape.Circle> circles = circlesLeftToRight(root);

    // Section 2: four circles, each with the expected radius
    check("four circles are displayed", 4, circles.size());
    for (int index = 0; index < circles.size(); index++) {
      check("circle " + (index + 1) + " radius is 30", Exercise31_01.CIRCLE_RADIUS,
          circles.get(index).getRadius());
    }

    // Section 3: the style class gives circles 1 and 2 a white fill and black stroke
    for (int index = 0; index < 2; index++) {
      javafx.scene.shape.Circle plain = circles.get(index);
      check("circle " + (index + 1) + " has class '" + Exercise31_01.PLAIN_CLASS + "'", true,
          plain.getStyleClass().contains(Exercise31_01.PLAIN_CLASS));
      check("circle " + (index + 1) + " fill is white", javafx.scene.paint.Color.WHITE, plain.getFill());
      check("circle " + (index + 1) + " stroke is black", javafx.scene.paint.Color.BLACK, plain.getStroke());
    }

    // Section 4: the ID selectors give circles 3 and 4 their red and green colors
    javafx.scene.shape.Circle redCircle = circles.get(2);
    javafx.scene.shape.Circle greenCircle = circles.get(3);
    check("circle 3 id is '" + Exercise31_01.RED_ID + "'", Exercise31_01.RED_ID, redCircle.getId());
    check("circle 3 fill is red", javafx.scene.paint.Color.RED, redCircle.getFill());
    check("circle 3 stroke is red", javafx.scene.paint.Color.RED, redCircle.getStroke());
    check("circle 4 id is '" + Exercise31_01.GREEN_ID + "'", Exercise31_01.GREEN_ID, greenCircle.getId());
    check("circle 4 fill is green", javafx.scene.paint.Color.GREEN, greenCircle.getFill());
    check("circle 4 stroke is green", javafx.scene.paint.Color.GREEN, greenCircle.getStroke());

    // Section 5: an ID must belong to exactly one node
    check("exactly one node has id 'redcircle'", 1, root.lookupAll("#" + Exercise31_01.RED_ID).size());
    check("exactly one node has id 'greencircle'", 1, root.lookupAll("#" + Exercise31_01.GREEN_ID).size());

    // Section 6: the border class puts a 5 px black border around the first pane
    javafx.scene.layout.Region borderedPane = (javafx.scene.layout.Region) root.getChildren().get(0);
    check("first pane has class 'border'", true,
        borderedPane.getStyleClass().contains(Exercise31_01.BORDER_CLASS));
    check("first pane has a border", true, borderedPane.getBorder() != null);
    if (borderedPane.getBorder() != null) {
      javafx.scene.layout.BorderStroke stroke = borderedPane.getBorder().getStrokes().get(0);
      check("border color is black", javafx.scene.paint.Color.BLACK, stroke.getTopStroke());
      check("border width is 5", 5.0, stroke.getWidths().getTop());
    }
    check("first pane contains circle 1", true, borderedPane.getChildrenUnmodifiable().contains(circles.get(0)));
    check("no other pane has a border", true,
        root.getChildren().subList(1, root.getChildren().size()).stream()
            .noneMatch(node -> node.getStyleClass().contains(Exercise31_01.BORDER_CLASS)));

    // Section 7: layout matches the sample run (300 x 250 window)
    double sceneWidth = Exercise31_01.SCENE_WIDTH;
    double sceneHeight = Exercise31_01.SCENE_HEIGHT;
    for (int index = 0; index < circles.size(); index++) {
      javafx.geometry.Bounds bounds = circles.get(index).localToScene(circles.get(index).getBoundsInLocal());
      check("circle " + (index + 1) + " is fully inside the window", true,
          bounds.getMinX() >= 0 && bounds.getMaxX() <= sceneWidth
              && bounds.getMinY() >= 0 && bounds.getMaxY() <= sceneHeight);
      checkClose("circle " + (index + 1) + " is vertically centered",
          sceneHeight / 2, (bounds.getMinY() + bounds.getMaxY()) / 2, 0.75);
      if (index > 0) {
        javafx.geometry.Bounds previous = circles.get(index - 1)
            .localToScene(circles.get(index - 1).getBoundsInLocal());
        check("circle " + (index + 1) + " does not overlap circle " + index, true,
            bounds.getMinX() >= previous.getMaxX());
      }
    }
    checkClose("bordered pane fills the full window height", sceneHeight, borderedPane.getHeight(), 0.01);
    javafx.geometry.Bounds paneBounds = borderedPane.localToScene(borderedPane.getBoundsInLocal());
    javafx.geometry.Bounds lastCircleBounds = circles.get(3).localToScene(circles.get(3).getBoundsInLocal());
    checkClose("row is horizontally centered (left margin equals right margin)",
        paneBounds.getMinX(), sceneWidth - lastCircleBounds.getMaxX(), 1.0);
  }
}
