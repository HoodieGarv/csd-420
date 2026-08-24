import java.io.*;
import java.util.Random;

/**
 * Name:       Garvin Stewart
 * Date:       Aug 23, 2026
 * Assignment: CSD 420 - Module 2 (Binary I/O)
 * Purpose:    Generates an array of five random integers and an array of
 *             five random doubles, then writes both arrays (as binary data)
 *             to a data file. If the file does not already exist it is
 *             created; if it does exist, the new arrays are appended to
 *             the end of the file rather than overwriting what's there.
 */
public class WriteDataFile {

    // TODO: replace "yourname" with your own last name before submitting,
    // e.g. "sampson_datafile.dat", to match the assignment naming convention.
    private static final String FILE_NAME = "yourname_datafile.dat";
    private static final int ARRAY_SIZE = 5;

    public static void main(String[] args) {
        int[] randomInts = generateRandomInts(ARRAY_SIZE);
        double[] randomDoubles = generateRandomDoubles(ARRAY_SIZE);

        System.out.println("Generated integers:");
        printArray(randomInts);
        System.out.println("Generated doubles:");
        printArray(randomDoubles);

        writeArraysToFile(FILE_NAME, randomInts, randomDoubles);
    }

    // Builds an array of random integers between 1 and 100 (inclusive)
    private static int[] generateRandomInts(int size) {
        Random rand = new Random();
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = rand.nextInt(100) + 1; // 1-100
        }
        return values;
    }

    // Builds an array of random doubles between 0.0 and 100.0
    private static double[] generateRandomDoubles(int size) {
        Random rand = new Random();
        double[] values = new double[size];
        for (int i = 0; i < size; i++) {
            values[i] = rand.nextDouble() * 100;
        }
        return values;
    }

    // Writes the integer array followed by the double array to fileName.
    // Passing "true" as the second argument to FileOutputStream turns on
    // append mode: if fileName already exists, new bytes are added after
    // the existing bytes; if it doesn't exist yet, Java creates it for us.
    private static void writeArraysToFile(String fileName, int[] ints, double[] doubles) {
        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(fileName, true)))) {

            for (int value : ints) {
                out.writeInt(value);
            }
            for (double value : doubles) {
                out.writeDouble(value);
            }

            System.out.println("Data successfully written (appended) to " + fileName);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    private static void printArray(int[] array) {
        for (int value : array) {
            System.out.print(value + " ");
        }
        System.out.println();
    }

    private static void printArray(double[] array) {
        for (double value : array) {
            System.out.printf("%.2f ", value);
        }
        System.out.println();
    }
}
