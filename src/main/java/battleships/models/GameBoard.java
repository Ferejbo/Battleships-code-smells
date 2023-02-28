package battleships.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The game board that stores the squares in the board. Provides all logic
 * needed with the board. One GameBoard object per player
 */
public class GameBoard {
    // Minimum value of width/height of the board
    private final int minDimension = 4;

    private Position[][] squares;

    /**
     * 
     * @param playerName
     * @param width      Width of board in squares
     * @param height     Height of board in squares
     */
    public GameBoard(int width, int height) throws IllegalArgumentException {
        this.evaluateDimensions(width, height);
        this.generateSquares(width, height);
    }

    /**
     * Creates the squares by the initialising squares and filling up the squares
     * array with the initialised squares
     * 
     * @param width  Width of board in squares
     * @param height Height of board in squares
     */
    private void generateSquares(int width, int height) throws IllegalArgumentException {
        this.evaluateDimensions(width, height);
        this.squares = new Position[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                squares[x][y] = new Position(x, y);
            }
        }
    }

    /**
     * Validates width and height
     * 
     * @param width  Width of board in squares
     * @param height Height of board in squares
     * @throws IllegalArgumentException When width/height is smaller than minimum or
     *                                  they are not equal
     */
    public void evaluateDimensions(int width, int height) throws IllegalArgumentException {
        if (width < minDimension || height < minDimension) {
            throw new IllegalArgumentException(
                    String.format("Width and height must be at least as big as minDimensions(%d) big", minDimension));
        }
        if (width != height) {
            throw new IllegalArgumentException("Board width and height must be equal");
        }
    }

    /**
     * Checks if game is over. Assumes the game is over and looks for contradiction
     * 
     * @return true/false based on if all squares containing a ship is hit
     */
    public boolean isGameOver() {
        for (Position[] posArr : squares) {
            for (Position pos : posArr) {
                if (!pos.getIsHit() && pos.getContainsShip())
                    return false;
            }
        }
        return true;
    }

    /**
     * Validates coordinate, then registers hit on Position object in the coordinate
     * 
     * @param x X coordinate on board
     * @param y Y coordinate on board
     */
    public void fireShot(int x, int y) throws IllegalArgumentException, IllegalStateException {
        validateCoordinates(x, y);
        squares[x][y].registerHit();
    }

    /**
     * First resets the board (the squares). Randomly places all battleships in a
     * brute force manner.
     * 
     * @param ships List of battleships to place
     */
    public void placeAllBattleships(List<Battleship> ships) throws IllegalArgumentException, IllegalStateException {
        Random ran = new Random();
        Boolean placeAlongX;
        int xBound;
        int yBound;
        int xPos;
        int yPos;

        emptyBoard();
        for (Battleship ship : ships) {
            while (true) {
                placeAlongX = true;
                if (ran.nextInt(2) == 0) {
                    placeAlongX = false;
                }
                // Determines max amount of start position in x and y based on if horizontal
                // placing or not
                xBound = placeAlongX ? squares.length - ship.getLength() + 1 : squares.length;
                yBound = placeAlongX ? squares[0].length : squares[0].length - ship.getLength() + 1;

                xPos = ran.nextInt(xBound);
                yPos = ran.nextInt(yBound);
                try {
                    placeBattleShip(xPos, yPos, ship, placeAlongX);
                    break;
                } catch (IllegalArgumentException | IllegalStateException ex) {

                }
            }
        }
    }

    /***
     * Validates and registers battleship placement on the relevant squares either
     * along xAxis or yAxis
     * 1: Validates that square is not occupied andd coordinates are not out of
     * bounds
     * 2: Registeres battleship on the relevant tiles
     * 
     * @param xPos        Start position on x-axis
     * @param yPos        Start position on y-axis
     * @param ship        Battleship to place. The length attribute in his class
     *                    determines how
     *                    many squares to validate/register
     * @param placeAlongX true if placement along x-axis, false if placement along
     *                    y-axis
     */
    public void placeBattleShip(int xPos, int yPos, Battleship ship, boolean placeAlongX)
            throws IllegalArgumentException, IllegalStateException {
        int itStart = placeAlongX ? xPos : yPos;
        boolean allPositionsValidated = false;

        int x = xPos;
        int y = yPos;
        for (int q = 0; q < 2; q++) {
            for (int i = itStart; i < itStart + ship.getLength(); i++) {
                if (placeAlongX) {
                    x = i;
                } else {
                    y = i;
                }

                if (allPositionsValidated) {
                    // 2
                    squares[x][y].registerShip();
                } else {
                    // 1
                    validateCoordinates(x, y);
                    Position targetPos = squares[x][y];
                    if (targetPos.getContainsShip()) {
                        // Denne er kanskje illegalState
                        throw new IllegalStateException(
                                String.format("(%d, %d) is already occupied by another boat", x, y));
                    }
                }
            }
            allPositionsValidated = true;
        }
    }

    public Position getSquare(int x, int y) throws IllegalArgumentException {
        validateCoordinates(x, y);
        return this.squares[x][y];
    }

    /**
     * Throws Exception when x/y is out of bounds of the square 2d-array
     * 
     * @param x x coordinate in board
     * @param y y coordinate in board
     * @throws IllegalArgumentException
     */
    public void validateCoordinates(int x, int y) throws IllegalArgumentException {
        if (x >= this.squares.length || y >= this.squares[0].length || x < 0 || y < 0)
            throw new IllegalArgumentException(
                    String.format("Coordinate arguments are out of bounds. (%d, %d) were received", x, y));
    }

    /** Resets board back to scratch */
    public void emptyBoard() {
        generateSquares(squares.length, squares[0].length);
    }

    public Position[][] getSquares() {
        return this.squares;
    }

    @Override
    public String toString() {
        String result = "";
        for (Position[] posArr : squares) {
            for (Position pos : posArr) {
                result += "[" + pos + "]";
            }
            result += "\n";
        }
        return result;
    }

    public String serialize() {
        String result = "";
        for (int x = 0; x < squares.length; x++) {
            for (int y = 0; y < squares[x].length; y++) {
                result += getSquare(x, y).serialize() + "-";
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof GameBoard))
            return false;

        GameBoard board = (GameBoard) o;

        if (squares.length != board.squares.length)
            return false;

        for (int x = 0; x < squares.length; x++) {
            if (squares[x].length != board.squares[x].length)
                return false;
            for (int y = 0; y < squares[x].length; y++) {
                if (!squares[x][y].equals(board.squares[x][y])) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {

    }
}
