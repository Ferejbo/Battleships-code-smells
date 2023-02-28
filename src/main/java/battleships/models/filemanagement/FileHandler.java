package battleships.models.filemanagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import battleships.models.Game;
import battleships.models.GameBoard;
import battleships.models.Player;
import battleships.models.Position;

public class FileHandler implements IFileHandler {

    @Override
    public Game readGameState(String filename) throws FileNotFoundException, IllegalStateException {
        File file = validateFileName(filename);
        if (!gameIsSaved(filename))
            throw new IllegalStateException("No game is saved");
        try (Scanner scanner = new Scanner(file)) {

            int boardWidth = 6;
            int boardHeight = 6;
            GameBoard board1 = new GameBoard(boardWidth, boardHeight);
            GameBoard board2 = new GameBoard(boardWidth, boardHeight);
            Player player1 = new Player("Player 1", board1, board2);
            Player player2 = new Player("Player 2", board2, board1);
            Player currentPlayer = player1;

            int lineIndex = 0;
            while (scanner.hasNextLine()) {
                String[] params = scanner.nextLine().split(";");
                if (lineIndex == 0) {
                    boardWidth = Integer.parseInt(params[0]);
                    boardHeight = Integer.parseInt(params[1]);
                } else if (lineIndex == 1) {
                    board1 = deserializeBoard(params[0], boardWidth, boardHeight);
                } else if (lineIndex == 2) {
                    board2 = deserializeBoard(params[0], boardWidth, boardHeight);
                } else if (lineIndex == 3) {
                    player1 = new Player(params[0], board1, board2);
                    player1.setShots(Integer.parseInt(params[1]));
                    player2 = new Player(params[3], board2, board1);
                    player2.setShots(Integer.parseInt(params[4]));
                    currentPlayer = Boolean.valueOf(params[2]) ? player1 : player2;
                }
                lineIndex++;
            }
            return new Game(player1, player2, currentPlayer, boardWidth, boardHeight);
        }
    }

    private GameBoard deserializeBoard(String boardSer, int width, int height) {
        GameBoard board = new GameBoard(width, height);
        String[] params = boardSer.split("-");
        for (String pos : params) {
            Position newPos = deserializePosition(pos);
            if (newPos.getIsHit())
                board.fireShot(newPos.getX(), newPos.getY());
            if (newPos.getContainsShip())
                board.getSquare(newPos.getX(), newPos.getY()).registerShip();
        }
        return board;
    }

    private Position deserializePosition(String posSer) {
        String[] params = posSer.split(":");
        Position pos = new Position(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
        if (Boolean.valueOf(params[2]))
            pos.registerHit();
        if (Boolean.valueOf(params[3]))
            pos.registerShip();
        return pos;
    }

    @Override
    public void writeGameState(String filename, Game game) throws FileNotFoundException {
        File file = validateFileName(filename);
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println(game.serialize());
        }
    }

    @Override
    public boolean gameIsSaved(String filename) throws FileNotFoundException {
        File file = validateFileName(filename);
        try (Scanner scanner = new Scanner(file)) {
            return scanner.hasNextLine();
        }
    }

    @Override
    public void deleteSave(String filename) throws FileNotFoundException {
        File file = validateFileName(filename);
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print("");
        }
    }

    private File validateFileName(String fileName) throws FileNotFoundException {
        File file = getReceiptFile(fileName);
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException("File must exist");
        }
        return file;
    }

    public File getReceiptFile(String filename) {
        return new File("target/classes/battleships/savedgame/" + filename + ".txt");
    }
}
