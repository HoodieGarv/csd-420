/*
 * Name: Garvin Stewart
 * Date: 10/04/2026
 * Assignment: CSD 420 Module 9 - JDBC Fan Database
 *
 * Purpose: Launches the fan viewer/updater window.
 */

public class FanApp extends javafx.application.Application {

  @Override
  public void start(javafx.stage.Stage primaryStage) {
    FanPane fanPane = new FanPane(new FanDatabase());

    primaryStage.setTitle("Fan Records");
    primaryStage.setScene(new javafx.scene.Scene(fanPane, 400, 280));
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
