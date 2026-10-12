/*
 * Name: Garvin Stewart
 * Date: 10/04/2026
 * Assignment: CSD 420 Module 9 - JDBC Fan Database
 *
 * Purpose: JDBC access layer that reads and updates rows in the existing "fans" table.
 *
 * Note: This class never creates or drops the table. It assumes "fans" already exists.
 */

public class FanDatabase {

  // Connection settings required by the assignment
  public static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/databasedb";
  public static final String DEFAULT_USER = "student1";
  public static final String DEFAULT_PASSWORD = "pass";

  // Every text column in the table is varchar(25)
  public static final int MAX_FIELD_LENGTH = 25;

  // Parameterized SQL (PreparedStatement) prevents SQL injection from user input
  private static final String SELECT_BY_ID_SQL =
      "SELECT ID, firstname, lastname, favoriteteam FROM fans WHERE ID = ?";
  private static final String UPDATE_SQL =
      "UPDATE fans SET firstname = ?, lastname = ?, favoriteteam = ? WHERE ID = ?";

  private final String url;
  private final String user;
  private final String password;

  // Default constructor uses the assignment's database, user, and password
  public FanDatabase() {
    this(DEFAULT_URL, DEFAULT_USER, DEFAULT_PASSWORD);
  }

  // Overloaded constructor lets the test code point at different credentials
  public FanDatabase(String url, String user, String password) {
    this.url = url;
    this.user = user;
    this.password = password;
  }

  // Opens a fresh connection; callers close it with try-with-resources
  private java.sql.Connection openConnection() throws java.sql.SQLException {
    return java.sql.DriverManager.getConnection(url, user, password);
  }

  /*
   * Returns the fan with the given ID, or null if no row matches.
   */
  public FanRecord findById(int id) throws java.sql.SQLException {
    try (java.sql.Connection connection = openConnection();
         java.sql.PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {

      statement.setInt(1, id);

      try (java.sql.ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return new FanRecord(
              resultSet.getInt("ID"),
              resultSet.getString("firstname"),
              resultSet.getString("lastname"),
              resultSet.getString("favoriteteam"));
        }
        return null;
      }
    }
  }

  /*
   * Writes the record's name/team values to the row with the record's ID.
   * Returns true if a row with that ID exists, false if no row matched.
   */
  public boolean update(FanRecord fan) throws java.sql.SQLException {
    validate(fan);

    try (java.sql.Connection connection = openConnection();
         java.sql.PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {

      statement.setString(1, fan.getFirstName());
      statement.setString(2, fan.getLastName());
      statement.setString(3, fan.getFavoriteTeam());
      statement.setInt(4, fan.getId());

      int rowsMatched = statement.executeUpdate();
      return rowsMatched > 0;
    }
  }

  /*
   * Rejects values the varchar(25) columns cannot hold, before they reach the database.
   */
  public static void validate(FanRecord fan) {
    validateField("First name", fan.getFirstName());
    validateField("Last name", fan.getLastName());
    validateField("Favorite team", fan.getFavoriteTeam());
  }

  private static void validateField(String fieldLabel, String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException(fieldLabel + " cannot be blank.");
    }
    if (value.length() > MAX_FIELD_LENGTH) {
      throw new IllegalArgumentException(
          fieldLabel + " must be " + MAX_FIELD_LENGTH + " characters or fewer.");
    }
  }
}
