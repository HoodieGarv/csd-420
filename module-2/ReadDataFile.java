import java.io.*;

/**
 * Name:       Garvin Stewart
 * Date:       Aug 23, 2026
 * Assignment: CSD 420 - Module 2 (Binary I/O)
 * Purpose:    Reads the binary data file created by WriteDataFile.java and
 *             displays its contents. Because WriteDataFile.java appends a
 *             new "batch" of 5 ints + 5 doubles every time it runs, this
 *             program keeps reading batches in a loop until it hits the
 *             end of the file (EOFException), so every batch that has
 *             ever been appended gets displayed, not just the most recent
 *             one.
 */
public class ReadDataFile {

    // TODO: replace "yourname" with your own last name to match the file
    // WriteDataFile.java created.
    private static final String FILE_NAME = "yourname_datafile.dat";
    private static final int ARRAY_SIZE = 5;

    public static void main(String[] args) {
        readArraysFromFile(FILE_NAME);
    }

    // Reads and prints every 5-int/5-double batch stored in fileName.
    private static void readArraysFromFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File not found: " + fileName);
            System.out.println("Run WriteDataFile.java first to create it.");
            return;
        }

        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(new FileInputStream(fileName)))) {

            int batchNumber = 1;
            while (true) {
                int[] ints = new int[ARRAY_SIZE];
                double[] doubles = new double[ARRAY_SIZE];

                for (int i = 0; i < ARRAY_SIZE; i++) {
                    ints[i] = in.readInt();
                }
                for (int i = 0; i < ARRAY_SIZE; i++) {
                    doubles[i] = in.readDouble();
                }

                System.out.println("Batch " + batchNumber + ":");
                System.out.print("  Integers: ");
                printArray(ints);
                System.out.print("  Doubles:  ");
                printArray(doubles);

                batchNumber++;
            }
        } catch (EOFException e) {
            // Expected condition: we've reached the end of the file and
            // there is nothing left to read.
            System.out.println("End of file reached. All data displayed.");
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
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
