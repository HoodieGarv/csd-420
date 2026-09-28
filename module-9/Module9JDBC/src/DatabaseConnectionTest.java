/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

/**
 *
 * @author garvinstewart
 */
public class DatabaseConnectionTest {

    // Connection settings required by the assignment
  private static final String DATABASE_URL =
      "jdbc:mysql://localhost:3306/databasedb"
      + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
  private static final String DATABASE_USER = "student1";
  private static final String DATABASE_PASSWORD = "pass";
 
  // ID reserved for the temporary write test so it never collides with the seed rows (24-26)
  private static final int TEST_ROW_ID = 999;
 
  public static void main(String[] args) {
    System.out.println("=== CSD 420 JDBC Setup Test ===");
 
    // try-with-resources closes the connection automatically, even if a test fails
    try (java.sql.Connection connection = java.sql.DriverManager.getConnection(
        DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
 
      printConnectionDetails(connection);
      printAddressTable(connection);
      runWriteTest(connection);
 
      System.out.println("\nRESULT: SUCCESS - Java, JDBC driver, and MySQL are configured correctly.");
 
    } catch (java.sql.SQLException sqlException) {
      System.out.println("\nRESULT: FAILED");
      System.out.println("SQLState: " + sqlException.getSQLState());
      System.out.println("Message : " + sqlException.getMessage());
      printTroubleshootingHint(sqlException);
    }
  }
 
  // Step 1: Show which server, driver, and account the program actually connected with
  private static void printConnectionDetails(java.sql.Connection connection)
      throws java.sql.SQLException {
    java.sql.DatabaseMetaData metaData = connection.getMetaData();
 
    System.out.println("\n[1] Connection established");
    System.out.println("    Database product : " + metaData.getDatabaseProductName()
        + " " + metaData.getDatabaseProductVersion());
    System.out.println("    JDBC driver      : " + metaData.getDriverName()
        + " " + metaData.getDriverVersion());
    System.out.println("    Connected as     : " + metaData.getUserName());
    System.out.println("    Current database : " + connection.getCatalog());
  }
 
  // Step 2: Read the seed rows created by the SQL setup script
  private static void printAddressTable(java.sql.Connection connection)
      throws java.sql.SQLException {
    String selectAllSql = "SELECT ID, FIRSTNAME, LASTNAME, STREET, CITY, STATE, ZIP "
        + "FROM address ORDER BY ID";
 
    System.out.println("\n[2] Reading rows from databasedb.address");
 
    try (java.sql.Statement statement = connection.createStatement();
         java.sql.ResultSet resultSet = statement.executeQuery(selectAllSql)) {
 
      int rowCount = 0;
 
      // ResultSet cursor starts before the first row; next() advances and returns false at the end
      while (resultSet.next()) {
        System.out.printf("    %-4d %-8s %-8s %-22s %-10s %-3s %s%n",
            resultSet.getInt("ID"),
            resultSet.getString("FIRSTNAME"),
            resultSet.getString("LASTNAME"),
            resultSet.getString("STREET"),
            resultSet.getString("CITY"),
            resultSet.getString("STATE"),
            resultSet.getString("ZIP"));
        rowCount++;
      }
 
      System.out.println("    Rows read: " + rowCount);
    }
  }
 
  // Step 3: Insert a row, read it back, then delete it - proves student1 has write privileges
  private static void runWriteTest(java.sql.Connection connection)
      throws java.sql.SQLException {
    System.out.println("\n[3] Write test (insert -> verify -> delete)");
 
    String insertSql = "INSERT INTO address VALUES (?, ?, ?, ?, ?, ?, ?)";
    String selectOneSql = "SELECT FIRSTNAME, LASTNAME FROM address WHERE ID = ?";
    String deleteSql = "DELETE FROM address WHERE ID = ?";
 
    // Clear any leftover test row from a previous interrupted run
    try (java.sql.PreparedStatement cleanupStatement = connection.prepareStatement(deleteSql)) {
      cleanupStatement.setInt(1, TEST_ROW_ID);
      cleanupStatement.executeUpdate();
    }
 
    // PreparedStatement binds values safely instead of concatenating them into SQL
    try (java.sql.PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
      insertStatement.setInt(1, TEST_ROW_ID);
      insertStatement.setString(2, "Stewart");
      insertStatement.setString(3, "Garvin");
      insertStatement.setString(4, "1000 Galvin Road South");
      insertStatement.setString(5, "Bellevue");
      insertStatement.setString(6, "NE");
      insertStatement.setString(7, "68005");
      System.out.println("    Rows inserted: " + insertStatement.executeUpdate());
    }
 
    try (java.sql.PreparedStatement selectStatement = connection.prepareStatement(selectOneSql)) {
      selectStatement.setInt(1, TEST_ROW_ID);
      try (java.sql.ResultSet resultSet = selectStatement.executeQuery()) {
        if (resultSet.next()) {
          System.out.println("    Read back    : " + resultSet.getString("FIRSTNAME")
              + " " + resultSet.getString("LASTNAME"));
        } else {
          throw new java.sql.SQLException("Inserted test row was not found on read-back.");
        }
      }
    }
 
    try (java.sql.PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
      deleteStatement.setInt(1, TEST_ROW_ID);
      System.out.println("    Rows deleted : " + deleteStatement.executeUpdate());
    }
  }
 
  // Map the most common setup failures to the fix, based on the error text
  private static void printTroubleshootingHint(java.sql.SQLException sqlException) {
    String message = String.valueOf(sqlException.getMessage()).toLowerCase();
 
    if (message.contains("no suitable driver")) {
      System.out.println("Hint: Connector/J jar is not on the classpath. "
          + "NetBeans: right-click Libraries > Add JAR/Folder.");
    } else if (message.contains("access denied")) {
      System.out.println("Hint: Wrong user/password, or student1 was created for a different host.");
    } else if (message.contains("unknown database")) {
      System.out.println("Hint: databasedb does not exist. Run setup_databasedb.sql as student1.");
    } else if (message.contains("doesn't exist")) {
      System.out.println("Hint: address table missing. Run the CREATE TABLE section of the SQL script.");
    } else if (message.contains("communications link failure") || message.contains("refused")) {
      System.out.println("Hint: MySQL server is not running or is not on port 3306.");
    } else if (message.contains("public key retrieval")) {
      System.out.println("Hint: Keep allowPublicKeyRetrieval=true in the URL (MySQL 8 authentication).");
    }
  }
}