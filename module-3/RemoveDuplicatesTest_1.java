import java.util.ArrayList;
import java.util.Random;

/**
 * Name: Garvin Stewart
 * Date: Aug 30, 2026
 * Assignment: Module 3.2 - Remove Duplicates from an ArrayList (Generics)
 *
 * Purpose: Demonstrates a generic static method that builds a new ArrayList
 * containing all values from an original ArrayList with duplicates removed.
 */
public class RemoveDuplicatesTest {

    /**
     * Builds and returns a NEW ArrayList containing every element from the
     * original list, preserving first-seen order, with duplicates removed.
     * Works for any reference type E because it relies only on equals().
     *
     * @param list the original ArrayList, possibly containing duplicates
     * @param <E>  the element type (generic - any object type works)
     * @return a new ArrayList with duplicate values removed
     */
    public static <E> ArrayList<E> removeDuplicates(ArrayList<E> list) {
        ArrayList<E> result = new ArrayList<>();

        // Walk through the original list one element at a time.
        for (E element : list) {
            // contains() uses equals() under the hood, so this works for
            // Integer, String, or any custom object with equals() defined.
            if (!result.contains(element)) {
                result.add(element);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        // Fill the original ArrayList with 50 random integers from 1 to 20.
        ArrayList<Integer> original = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < 50; i++) {
            original.add(rand.nextInt(20) + 1); // range: 1 to 20 inclusive
        }

        // Call the generic method - E is inferred as Integer here.
        ArrayList<Integer> unique = removeDuplicates(original);

        // Display results.
        System.out.println("Original list (" + original.size() + " values):");
        System.out.println(original);

        System.out.println("\nList with duplicates removed (" + unique.size() + " values):");
        System.out.println(unique);
    }
}
