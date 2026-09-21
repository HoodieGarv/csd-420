// Name: Garvin Stewart
// Date: 09/20/2026
// Assignment: CSD 420 Module 7 Programming Assignment
// Purpose: Display four circles styled by an external CSS style class and IDs.

public class Exercise31_01 extends javafx.application.Application {
  // Names shared with the test class so both always use the same spelling
  public static final String STYLESHEET_NAME = "mystyle.css";
  public static final String PLAIN_CLASS = "plaincircle";
  public static final String BORDER_CLASS = "border";
  public static final String RED_ID = "redcircle";
  public static final String GREEN_ID = "greencircle";

  // Sizes taken from the sample run (300 x 250 window, radius-30 circles, 5 px gaps)
  public static final double SCENE_WIDTH = 300;
  public static final double SCENE_HEIGHT = 250;
  public static final double CIRCLE_RADIUS = 30;
  public static final double SPACING = 5;

  // Builds the scene graph; separated from start() so the test class can call it directly
  public static javafx.scene.layout.HBox buildLayout() {
    javafx.scene.layout.HBox row = new javafx.scene.layout.HBox(SPACING);
    row.setAlignment(javafx.geometry.Pos.CENTER); // Center the row of circles in the window

    // Circle 1: white/black via the style class, alone inside a bordered pane
    javafx.scene.shape.Circle borderedCircle = new javafx.scene.shape.Circle(CIRCLE_RADIUS);
    borderedCircle.getStyleClass().add(PLAIN_CLASS);
    javafx.scene.layout.StackPane borderedPane = new javafx.scene.layout.StackPane(borderedCircle);
    borderedPane.getStyleClass().add(BORDER_CLASS);

    // Circle 2: the same style class again shows a class can be reused
    javafx.scene.shape.Circle plainCircle = new javafx.scene.shape.Circle(CIRCLE_RADIUS);
    plainCircle.getStyleClass().add(PLAIN_CLASS);

    // Circle 3: red through a style ID (an ID belongs to only one node)
    javafx.scene.shape.Circle redCircle = new javafx.scene.shape.Circle(CIRCLE_RADIUS);
    redCircle.setId(RED_ID);

    // Circle 4: green through a different style ID
    javafx.scene.shape.Circle greenCircle = new javafx.scene.shape.Circle(CIRCLE_RADIUS);
    greenCircle.setId(GREEN_ID);

    row.getChildren().addAll(borderedPane, plainCircle, redCircle, greenCircle);
    return row;
  }

  @Override
  public void start(javafx.stage.Stage primaryStage) {
    javafx.scene.Scene scene = new javafx.scene.Scene(buildLayout(), SCENE_WIDTH, SCENE_HEIGHT);
    scene.getStylesheets().add(STYLESHEET_NAME); // Load the external style sheet

    primaryStage.setTitle("Exercise31_01");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
