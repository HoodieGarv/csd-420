/*
 * Name: Garvin Stewart
 * Date: 10/04/2026
 * Assignment: CSD 420 Module 9 - JDBC Fan Database
 *
 * Purpose: JavaFX form with Display and Update buttons for viewing and editing a fan record.
 */

public class FanPane extends javafx.scene.layout.GridPane {

  private final FanDatabase database;

  // Form controls
  private final javafx.scene.control.TextField idField = new javafx.scene.control.TextField();
  private final javafx.scene.control.TextField firstNameField = new javafx.scene.control.TextField();
  private final javafx.scene.control.TextField lastNameField = new javafx.scene.control.TextField();
  private final javafx.scene.control.TextField favoriteTeamField = new javafx.scene.control.TextField();
  private final javafx.scene.control.Button displayButton = new javafx.scene.control.Button("Display");
  private final javafx.scene.control.Button updateButton = new javafx.scene.control.Button("Update");
  private final javafx.scene.control.Label statusLabel =
      new javafx.scene.control.Label("Enter an ID and click Display.");

  // ID of the record currently shown; null until a successful Display
  private Integer loadedId = null;

  public FanPane(FanDatabase database) {
    this.database = database;

    // Layout: labels in column 0, inputs in column 1
    setPadding(new javafx.geometry.Insets(15));
    setHgap(10);
    setVgap(10);

    add(new javafx.scene.control.Label("ID:"), 0, 0);
    add(idField, 1, 0);
    add(new javafx.scene.control.Label("First Name:"), 0, 1);
    add(firstNameField, 1, 1);
    add(new javafx.scene.control.Label("Last Name:"), 0, 2);
    add(lastNameField, 1, 2);
    add(new javafx.scene.control.Label("Favorite Team:"), 0, 3);
    add(favoriteTeamField, 1, 3);

    javafx.scene.layout.HBox buttonRow = new javafx.scene.layout.HBox(10, displayButton, updateButton);
    add(buttonRow, 1, 4);

    statusLabel.setWrapText(true);
    add(statusLabel, 0, 5, 2, 1);

    // Button wiring
    displayButton.setOnAction(event -> handleDisplay());
    updateButton.setOnAction(event -> handleUpdate());
  }

  /*
   * Display: look up the ID typed by the user and fill the form with that record.
   */
  public void handleDisplay() {
    Integer id = parseIdField();
    if (id == null) {
      return;
    }

    try {
      FanRecord fan = database.findById(id);

      if (fan == null) {
        clearFanFields();
        loadedId = null;
        statusLabel.setText("No fan found with ID " + id + ".");
        return;
      }

      firstNameField.setText(fan.getFirstName());
      lastNameField.setText(fan.getLastName());
      favoriteTeamField.setText(fan.getFavoriteTeam());
      loadedId = id;
      statusLabel.setText("Displaying fan ID " + id + ".");
    } catch (java.sql.SQLException ex) {
      statusLabel.setText("Database error: " + ex.getMessage());
    }
  }

  /*
   * Update: save the edited form values back to the database.
   * Only allowed for the record that was last displayed, so a changed ID field
   * cannot overwrite a different fan with this fan's data.
   */
  public void handleUpdate() {
    Integer id = parseIdField();
    if (id == null) {
      return;
    }

    if (loadedId == null || !loadedId.equals(id)) {
      statusLabel.setText("Click Display for ID " + id + " before updating it.");
      return;
    }

    FanRecord editedFan = new FanRecord(
        id,
        firstNameField.getText().trim(),
        lastNameField.getText().trim(),
        favoriteTeamField.getText().trim());

    try {
      boolean updated = database.update(editedFan);
      if (updated) {
        statusLabel.setText("Fan ID " + id + " updated.");
      } else {
        statusLabel.setText("No fan found with ID " + id + "; nothing updated.");
      }
    } catch (IllegalArgumentException ex) {
      statusLabel.setText(ex.getMessage());
    } catch (java.sql.SQLException ex) {
      statusLabel.setText("Database error: " + ex.getMessage());
    }
  }

  // Converts the ID field to an int; reports a message and returns null if invalid
  private Integer parseIdField() {
    String idText = idField.getText().trim();
    try {
      return Integer.parseInt(idText);
    } catch (NumberFormatException ex) {
      statusLabel.setText("ID must be a whole number.");
      return null;
    }
  }

  private void clearFanFields() {
    firstNameField.clear();
    lastNameField.clear();
    favoriteTeamField.clear();
  }

  // Accessors used by FanTest to drive the interface
  public javafx.scene.control.TextField getIdField() { return idField; }
  public javafx.scene.control.TextField getFirstNameField() { return firstNameField; }
  public javafx.scene.control.TextField getLastNameField() { return lastNameField; }
  public javafx.scene.control.TextField getFavoriteTeamField() { return favoriteTeamField; }
  public javafx.scene.control.Button getDisplayButton() { return displayButton; }
  public javafx.scene.control.Button getUpdateButton() { return updateButton; }
  public javafx.scene.control.Label getStatusLabel() { return statusLabel; }
}
