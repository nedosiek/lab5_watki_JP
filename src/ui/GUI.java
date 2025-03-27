package ui;

import logic.GameBoardTorus;
import logic.InputFileLoading;

import java.awt.*;
import javax.swing.*;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class GUI {
    private final GameBoardTorus board;
    private final int threadCount;
    private JFrame frame;
    private JPanel gridPanel;
    private JButton[][] cells;
    private Color[] threadColors;

    public GUI(GameBoardTorus board, int threadCount) {
        this.board = board;
        this.threadCount = threadCount;
        this.threadColors = generateThreadColors(threadCount);
    }

    public void createAndShowGUI() {
        frame = new JFrame("Conway's Game of Life");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(board.getCols() *InputFileLoading.CELL_SIZE, board.getRows() * InputFileLoading.CELL_SIZE);

        gridPanel = new JPanel(new GridLayout(board.getRows(), board.getCols()));
        cells = new JButton[board.getRows()][board.getCols()];

        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                JButton cell = new JButton();
                cell.setBackground(board.isAlive(row, col) ? threadColors[row % threadCount] : InputFileLoading.DEAD_COLOR);
                cell.setEnabled(false);
                cells[row][col] = cell;
                gridPanel.add(cell);
            }
        }

        frame.add(gridPanel);
        frame.setVisible(true);

        new Thread(this::runGame).start();
    }

    private Color[] generateThreadColors(int threadCount) {
        Random random = new Random();
        Color[] colors = new Color[threadCount];
        for (int i = 0; i < threadCount; i++) {
            Color color;
            do {
                color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            } while (color.equals(InputFileLoading.DEAD_COLOR));
            colors[i] = color;
        }
        return colors;
    }

    private void runGame() {
        for (int iteration = 0; iteration < board.getIterations(); iteration++) {
            boolean[][] nextState = new boolean[board.getRows()][board.getCols()];
            CountDownLatch latch = new CountDownLatch(threadCount);

            int baseRowsPerThread = board.getRows() / threadCount;
            int extraRows = board.getRows() % threadCount;

            int currentRow = 0;

            for (int t = 0; t < threadCount; t++) {
                int rowsPerThread = baseRowsPerThread + (t < extraRows ? 1 : 0);
                int startRow = currentRow;
                int endRow = startRow + rowsPerThread;
                currentRow = endRow;
                //int startRow = t * rowsPerThread;
                //int endRow = (t == threadCount - 1) ? board.getRows() : startRow + rowsPerThread;
                Color threadColor = threadColors[t];

                int finalT = t;
                new Thread(() -> {
                    int totalRowsProcessed = 0;
                    int totalColsProcessed = 0;

                    for (int row = startRow; row < endRow; row++) {
                        for (int col = 0; col < board.getCols(); col++) {
                            int aliveNeighbors = countAliveNeighbors(row, col);
                            nextState[row][col] = (board.isAlive(row, col) && (aliveNeighbors == 2 || aliveNeighbors == 3))
                                    || (!board.isAlive(row, col) && aliveNeighbors == 3);

                            int finalRow = row;
                            int finalCol = col;
                            SwingUtilities.invokeLater(() -> {
                                if (nextState[finalRow][finalCol]) {
                                    cells[finalRow][finalCol].setBackground(threadColor);
                                } else {
                                    cells[finalRow][finalCol].setBackground(InputFileLoading.DEAD_COLOR);
                                }
                            });

                            totalColsProcessed++;
                        }
                        totalRowsProcessed++;
                    }

                    // Opis aktualnych wątków
                    System.out.printf("tid %d: rows: %d:%d (%d) cols: 0:%d (%d)\n", finalT, startRow, endRow - 1, totalRowsProcessed, board.getCols() - 1, totalColsProcessed/rowsPerThread);

                    latch.countDown();
                }).start();
            }

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            board.setNextState(nextState);

            try {
                Thread.sleep(200); // Opóźnienie
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private int countAliveNeighbors(int row, int col) {
        int alive = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr != 0 || dc != 0) {
                    alive += board.isAlive(row + dr, col + dc) ? 1 : 0;
                }
            }
        }
        return alive;
    }
}
