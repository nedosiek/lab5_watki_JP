package logic;

public class GameBoardTorus {
    private final int rows;
    private final int cols;
    private final int iterations;
    private boolean[][] current;

    public GameBoardTorus(int rows, int cols, int iterations, boolean[][] initialState) {
        this.rows = rows;
        this.cols = cols;
        this.iterations = iterations;
        this.current = initialState;
    }

    public synchronized boolean[][] getCurrentState() {
        return current;
    }

    public void setNextState(boolean[][] nextState) {
        this.current = nextState;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getIterations() {
        return iterations;
    }

    public boolean isAlive(int row, int col) {
        row = (row + rows) % rows;
        col = (col + cols) % cols;
        return current[row][col];
    }
}
