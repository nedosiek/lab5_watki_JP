package ui;

import logic.GameBoardTorus;
import logic.InputFileLoading;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Argumenty wejściowe: plik oraz ilość wątków
        if (args.length != 2 || Integer.parseInt(args[1])==0) {
            System.out.println("Usage: java Main <config_file> <thread_count>");
            return;
        }

        String configFile = args[0];
        int threadCount = Integer.parseInt(args[1]);

        GameBoardTorus board = InputFileLoading.loadConfig(configFile);
        if (board == null) {
            System.err.println("Failed to load configuration file.");
            return;
        }

        if (board.getRows() == 0 || board.getCols() == 0) { // Sprawdzanie poprawności planszy
            System.err.println("Invalid board configuration.");
            return;
        }

        GUI ui = new GUI(board, threadCount);
        SwingUtilities.invokeLater(ui::createAndShowGUI);
    }
}
