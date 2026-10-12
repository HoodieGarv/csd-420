/*
 * Name: Garvin Stewart
 * Date: 10/04/2026
 * Assignment: CSD 420 Module 9 - JDBC Fan Database
 *
 * Purpose: Immutable data holder for one row of the "fans" table.
 */

public class FanRecord {

  // Column values for a single fan row
  private final int id;
  private final String firstName;
  private final String lastName;
  private final String favoriteTeam;

  public FanRecord(int id, String firstName, String lastName, String favoriteTeam) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.favoriteTeam = favoriteTeam;
  }

  public int getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getFavoriteTeam() {
    return favoriteTeam;
  }

  @Override
  public String toString() {
    return "FanRecord[id=" + id + ", firstName=" + firstName
        + ", lastName=" + lastName + ", favoriteTeam=" + favoriteTeam + "]";
  }
}
