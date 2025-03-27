package logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class InputFileLoading {
    public static final int CELL_SIZE = 20;
    public static final java.awt.Color DEAD_COLOR = java.awt.Color.WHITE;

    public static GameBoardTorus loadConfig(String filePath) {
        System.out.println("Loading configuration file: " + filePath);
        try (Scanner scanner = new Scanner(new File(filePath))) {
            int rows = scanner.nextInt();
            int cols = scanner.nextInt();
            int iterations = scanner.nextInt();
            int liveCellsCount = scanner.nextInt();
            System.out.println("Rows: " + rows + ", Cols: " + cols + ", Iterations: " + iterations);
            System.out.println("Alive cells count: " + liveCellsCount);

            boolean[][] initialBoard = new boolean[rows][cols];
            for (int i = 0; i < liveCellsCount; i++) {
                int row = scanner.nextInt();
                int col = scanner.nextInt();
                initialBoard[row][col] = true;
                System.out.println("Setting cell alive at: (" + row + ", " + col + ")");
            }

            return new GameBoardTorus(rows, cols, iterations, initialBoard);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();  // pełny stack trace dla lepszej diagnostyki
        }
            return null;
        }
    }
