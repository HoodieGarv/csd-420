/*
 * Name: Garvin Stewart
 * Date: 10/04/2026 
 * Assignment: CSD 420 Module 9 - JDBC Fan Database
 *
 * Purpose: Tests every FanDatabase method and both FanPane buttons against the live "fans" table.
 *
 * Notes:
 *  - Does not create or drop the table. It discovers IDs from whatever data is already loaded.
 *  - Every test that changes a row restores the original values in a finally block.
 */

public class FanTest {

  private static int passed = 0;
  private static int failed = 0;

  private static final FanDatabase database = new FanDatabase();

  // IDs discovered from the existing table at startup
  private static int existingId;
  private static Integer secondExistingId;   // null if the table has only one row
  private static int missingId;

  public static void main(String[] args) throws Exception {
    discoverTestIds();

    // ---- Data-layer tests ----
    testFindExistingRecord();
    testFindMissingRecordReturnsNull();
    testUpdateExistingRecord();
    testUpdateMissingRecordReturnsFalse();
    testValidationRejectsTooLongField();
    testValidationRejectsBlankField();
    testBadCredentialsThrowSqlException();

    // ---- Interface tests (run on the JavaFX thread) ----
    javafx.application.Platform.startup(() -> { });
    javafx.application.Platform.setImplicitExit(false);

    testGuiDisplayFillsFields();
    testGuiDisplayMissingId();
    testGuiDisplayNonNumericId();
    testGuiUpdateSavesChanges();
    testGuiUpdateWithoutDisplayIsRejected();
    testGuiUpdateAfterIdChangeIsRejected();
    testGuiUpdateTooLongIsRejected();

    System.out.println();
    System.out.println("Passed: " + passed + "  Failed: " + failed);

    javafx.application.Platform.exit();
    System.exit(failed == 0 ? 0 : 1);
  }

  /* ===================== Setup helpers ===================== */

  // Reads IDs from the existing data so tests work on any populated table
  private static void discoverTestIds() throws java.sql.SQLException {
    try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
             FanDatabase.DEFAULT_URL, FanDatabase.DEFAULT_USER, FanDatabase.DEFAULT_PASSWORD);
         java.sql.Statement statement = connection.createStatement();
         java.sql.ResultSet resultSet = statement.executeQuery(
             "SELECT ID FROM fans ORDER BY ID LIMIT 2")) {

      if (!resultSet.next()) {
        throw new IllegalStateException("The fans table is empty; populate it before testing.");
      }
      existingId = resultSet.getInt("ID");
      secondExistingId = resultSet.next() ? resultSet.getInt("ID") : null;
    }

    try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
             FanDatabase.DEFAULT_URL, FanDatabase.DEFAULT_USER, FanDatabase.DEFAULT_PASSWORD);
         java.sql.Statement statement = connection.createStatement();
         java.sql.ResultSet resultSet = statement.executeQuery("SELECT MAX(ID) + 1 FROM fans")) {
      resultSet.next();
      missingId = resultSet.getInt(1);
    }

    System.out.println("Using existingId=" + existingId + ", secondExistingId="
        + secondExistingId + ", missingId=" + missingId);
    System.out.println();
  }

  private static void check(boolean condition, String testName) {
    if (condition) {
      passed++;
      System.out.println("PASS  " + testName);
    } else {
      failed++;
      System.out.println("FAIL  " + testName);
    }
  }

  // Runs a block on the JavaFX Application Thread and waits for it to finish
  private static void runOnFxThread(Runnable task) throws Exception {
    java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
    Throwable[] error = new Throwable[1];

    javafx.application.Platform.runLater(() -> {
      try {
        task.run();
      } catch (Throwable t) {
        error[0] = t;
      } finally {
        latch.countDown();
      }
    });

    if (!latch.await(10, java.util.concurrent.TimeUnit.SECONDS)) {
      throw new IllegalStateException("JavaFX task timed out.");
    }
    if (error[0] != null) {
      throw new RuntimeException(error[0]);
    }
  }

  private static boolean sameValues(FanRecord a, FanRecord b) {
    return a.getId() == b.getId()
        && a.getFirstName().equals(b.getFirstName())
        && a.getLastName().equals(b.getLastName())
        && a.getFavoriteTeam().equals(b.getFavoriteTeam());
  }

  /* ===================== Data-layer tests ===================== */

  private static void testFindExistingRecord() throws Exception {
    FanRecord fan = database.findById(existingId);
    check(fan != null && fan.getId() == existingId, "findById returns the existing record");
  }

  private static void testFindMissingRecordReturnsNull() throws Exception {
    check(database.findById(missingId) == null, "findById returns null for a missing ID");
  }

  private static void testUpdateExistingRecord() throws Exception {
    FanRecord original = database.findById(existingId);
    try {
      FanRecord changed = new FanRecord(existingId, original.getFirstName(),
          original.getLastName(), "TestTeamXYZ");
      boolean result = database.update(changed);
      FanRecord reread = database.findById(existingId);
      check(result && "TestTeamXYZ".equals(reread.getFavoriteTeam()),
          "update changes an existing record");
    } finally {
      database.update(original);   // restore
    }
    check(sameValues(original, database.findById(existingId)),
        "update restores original values (cleanup check)");
  }

  private static void testUpdateMissingRecordReturnsFalse() throws Exception {
    boolean result = database.update(new FanRecord(missingId, "No", "Body", "None"));
    check(!result && database.findById(missingId) == null,
        "update returns false and inserts nothing for a missing ID");
  }

  private static void testValidationRejectsTooLongField() {
    String twentySixChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    try {
      FanDatabase.validate(new FanRecord(existingId, twentySixChars, "Last", "Team"));
      check(false, "validate rejects a 26-character field");
    } catch (IllegalArgumentException ex) {
      check(true, "validate rejects a 26-character field");
    }
  }

  private static void testValidationRejectsBlankField() {
    try {
      FanDatabase.validate(new FanRecord(existingId, "First", "  ", "Team"));
      check(false, "validate rejects a blank field");
    } catch (IllegalArgumentException ex) {
      check(true, "validate rejects a blank field");
    }
  }

  private static void testBadCredentialsThrowSqlException() {
    FanDatabase badDatabase = new FanDatabase(FanDatabase.DEFAULT_URL, "student1", "wrongpass");
    try {
      badDatabase.findById(existingId);
      check(false, "bad password raises SQLException");
    } catch (java.sql.SQLException ex) {
      check(true, "bad password raises SQLException");
    }
  }

  /* ===================== Interface tests ===================== */

  private static void testGuiDisplayFillsFields() throws Exception {
    FanRecord expected = database.findById(existingId);
    boolean[] ok = new boolean[1];

    runOnFxThread(() -> {
      FanPane pane = new FanPane(database);
      pane.getIdField().setText(String.valueOf(existingId));
      pane.getDisplayButton().fire();

      ok[0] = expected.getFirstName().equals(pane.getFirstNameField().getText())
          && expected.getLastName().equals(pane.getLastNameField().getText())
          && expected.getFavoriteTeam().equals(pane.getFavoriteTeamField().getText());
    });
    check(ok[0], "Display button fills fields with the matching record");
  }

  private static void testGuiDisplayMissingId() throws Exception {
    boolean[] ok = new boolean[1];
    runOnFxThread(() -> {
      FanPane pane = new FanPane(database);
      pane.getFirstNameField().setText("stale");
      pane.getIdField().setText(String.valueOf(missingId));
      pane.getDisplayButton().fire();

      ok[0] = pane.getStatusLabel().getText().startsWith("No fan found")
          && pane.getFirstNameField().getText().isEmpty();
    });
    check(ok[0], "Display with a missing ID reports it and clears fields");
  }

  private static void testGuiDisplayNonNumericId() throws Exception {
    boolean[] ok = new boolean[1];
    runOnFxThread(() -> {
      FanPane pane = new FanPane(database);
      pane.getIdField().setText("abc");
      pane.getDisplayButton().fire();
      ok[0] = pane.getStatusLabel().getText().contains("whole number");
    });
    check(ok[0], "Display with a non-numeric ID shows an error");
  }

  private static void testGuiUpdateSavesChanges() throws Exception {
    FanRecord original = database.findById(existingId);
    try {
      runOnFxThread(() -> {
        FanPane pane = new FanPane(database);
        pane.getIdField().setText(String.valueOf(existingId));
        pane.getDisplayButton().fire();
        pane.getFavoriteTeamField().setText("GuiTestTeam");
        pane.getUpdateButton().fire();
      });
      FanRecord reread = database.findById(existingId);
      check("GuiTestTeam".equals(reread.getFavoriteTeam())
          && original.getFirstName().equals(reread.getFirstName()),
          "Update button saves edited fields to the database");
    } finally {
      database.update(original);   // restore
    }
  }

  private static void testGuiUpdateWithoutDisplayIsRejected() throws Exception {
    FanRecord original = database.findById(existingId);
    try {
      String[] status = new String[1];
      runOnFxThread(() -> {
        FanPane pane = new FanPane(database);
        pane.getIdField().setText(String.valueOf(existingId));
        pane.getFirstNameField().setText("Should");
        pane.getLastNameField().setText("NotSave");
        pane.getFavoriteTeamField().setText("Anything");
        pane.getUpdateButton().fire();
        status[0] = pane.getStatusLabel().getText();
      });
      check(status[0].startsWith("Click Display")
          && sameValues(original, database.findById(existingId)),
          "Update before Display is rejected and nothing changes");
    } finally {
      database.update(original);
    }
  }

  private static void testGuiUpdateAfterIdChangeIsRejected() throws Exception {
    if (secondExistingId == null) {
      System.out.println("SKIP  Update after ID change (table has only one row)");
      return;
    }
    FanRecord secondOriginal = database.findById(secondExistingId);
    try {
      runOnFxThread(() -> {
        FanPane pane = new FanPane(database);
        pane.getIdField().setText(String.valueOf(existingId));
        pane.getDisplayButton().fire();
        pane.getIdField().setText(String.valueOf(secondExistingId));   // user changes ID
        pane.getUpdateButton().fire();
      });
      check(sameValues(secondOriginal, database.findById(secondExistingId)),
          "Update after changing the ID does not overwrite another fan");
    } finally {
      database.update(secondOriginal);
    }
  }

  private static void testGuiUpdateTooLongIsRejected() throws Exception {
    FanRecord original = database.findById(existingId);
    try {
      String[] status = new String[1];
      runOnFxThread(() -> {
        FanPane pane = new FanPane(database);
        pane.getIdField().setText(String.valueOf(existingId));
        pane.getDisplayButton().fire();
        pane.getLastNameField().setText("ThisLastNameIsWayTooLongForIt");
        pane.getUpdateButton().fire();
        status[0] = pane.getStatusLabel().getText();
      });
      check(status[0].contains("25 characters")
          && sameValues(original, database.findById(existingId)),
          "Update with a 26+ character field is rejected");
    } finally {
      database.update(original);
    }
  }
}
