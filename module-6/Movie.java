/*
 * Name: Garvin Stewart
 * Date: Sept 13, 2026
 * Assignment: Module 6 Programming Assignment - supporting class
 *
 * Purpose: A small custom class used to test bubbleSortComparable() and
 * bubbleSortComparator() on something other than a built-in wrapper type.
 * Implements Comparable<Movie> so it has a natural ordering (by rating),
 * while a Comparator can still be supplied separately to sort by title
 * instead.
 */
public class Movie implements Comparable<Movie> {

  private String title;
  private double rating;

  public Movie(String title, double rating) {
    this.title = title;
    this.rating = rating;
  }

  public String getTitle() {
    return title;
  }

  public double getRating() {
    return rating;
  }

  // Natural ordering: ascending by rating. This is what
  // bubbleSortComparable() uses when no Comparator is supplied.
  @Override
  public int compareTo(Movie other) {
    return Double.compare(this.rating, other.rating);
  }

  // Used by printArray() so test output is readable instead of showing
  // an object reference like Movie@1b6d3586.
  @Override
  public String toString() {
    return title + " (" + rating + ")";
  }
}
