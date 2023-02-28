package battleships.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameBoardTest {

    private final int boardSize = 6;
    private Position[][] expectedSquares;
    private Battleship ship1;
    private Battleship ship2;
    private GameBoard actualBoard;

    // Helper hash map mapping out different x, y combinations when testing out of
    // bounds checking
    private HashMap<Integer, Integer> xToYCoordinatesMap;

    @BeforeEach
    void setup() {
        ship1 = new Battleship(4);
        ship2 = new Battleship(3);
        actualBoard = new GameBoard(boardSize, boardSize);
        expectedSquares = new Position[boardSize][boardSize];
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                expectedSquares[x][y] = new Position(x, y);
            }
        }

        xToYCoordinatesMap = new HashMap<>();
        xToYCoordinatesMap.put(-1, 2);
        xToYCoordinatesMap.put(2, -1);
        xToYCoordinatesMap.put(-3, -5);
        xToYCoordinatesMap.put(boardSize, -5);
        xToYCoordinatesMap.put(-6, boardSize);
        xToYCoordinatesMap.put(boardSize + 1, boardSize);
    }

    /**
     * Ensures that the two 2d position arrays have equal attributes to test that
     * they are equal
     */
    void testSquares(Position[][] expected, Position[][] actual) {
        // Testing that the size(s) matches in the 2d-arrays
        assertEquals(expected.length, actual.length);
        for (int x = 0; x < expected.length; x++) {
            assertEquals(expected[x].length, actual[x].length);
        }

        // Testing that values matches
        for (int x = 0; x < expected.length; x++) {
            for (int y = 0; y < expected[x].length; y++) {
                Position expectedPos = expected[x][y];
                Position actualPos = actual[x][y];
                assertEquals(expectedPos.getX(), actualPos.getX());
                assertEquals(expectedPos.getY(), actualPos.getY());
                assertEquals(expectedPos.getContainsShip(), actualPos.getContainsShip());
                assertEquals(expectedPos.getIsHit(), actualPos.getIsHit());
            }
        }
    }

    /**
     * Generates some ships in actual and expected squares
     * 
     * @return List with all the positions that has boats
     */
    List<int[]> generateShips() {
        List<int[]> shipPos = new ArrayList<>();
        actualBoard.placeBattleShip(2, 3, ship1, true);
        for (int x = 2; x < 2 + ship1.getLength(); x++) {
            expectedSquares[x][3].registerShip();
            int[] pos = { x, 3 };
            shipPos.add(pos);
        }
        actualBoard.placeBattleShip(1, 2, ship2, false);
        for (int y = 2; y < 2 + ship2.getLength(); y++) {
            expectedSquares[1][y].registerShip();
            int[] pos = { 1, y };
            shipPos.add(pos);
        }
        return shipPos;
    }

    @Test
    void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new GameBoard(-1, -1),
                "Test that width/height cant be smaller than minimum allowed (currently 4)");
        assertThrows(IllegalArgumentException.class, () -> new GameBoard(3, 3),
                "Test that width/height cant be smaller than minimum allowed (currently 4)");
        assertThrows(IllegalArgumentException.class, () -> new GameBoard(5, 6),
                "Board and width not equal exception");

        GameBoard actualBoard = new GameBoard(boardSize, boardSize);
        // Testing that generated actualBoard is correct
        testSquares(expectedSquares, actualBoard.getSquares());
    }

    @Test
    void testPlaceBattleship() {
        // Check placement along x-axis
        actualBoard.placeBattleShip(2, 3, ship1, true);
        for (int x = 2; x < 2 + ship1.getLength(); x++) {
            expectedSquares[x][3].registerShip();
        }
        testSquares(expectedSquares, actualBoard.getSquares());
        assertThrows(IllegalStateException.class, () -> actualBoard.placeBattleShip(0, 3, ship2, true),
                "Test that state-exception thrown when we try to register battleship on square(s) that is already occupied");
        // Check that the actualBoard remains unchanged
        testSquares(expectedSquares, actualBoard.getSquares());

        assertThrows(IllegalArgumentException.class, () -> actualBoard.placeBattleShip(9, 3, ship2, true),
                "Test that argument exception is thrown when x-coordinate is out of bounds");
        assertThrows(IllegalArgumentException.class, () -> actualBoard.placeBattleShip(0, 9, ship2, true),
                "Test that argument exception is thrown when y-coordinate is out of bounds");
        assertThrows(IllegalArgumentException.class, () -> actualBoard.placeBattleShip(-2, -2, ship2, true),
                "Test that argument exception is thrown when y- and x-coordinate is out of bounds");
        assertThrows(IllegalArgumentException.class, () -> actualBoard.placeBattleShip(4, 2, ship2, true),
                "Test that argument exception is thrown when x-coordinate + ship length is out of bounds");
        // Check that the actualBoard remains unchanged
        testSquares(expectedSquares, actualBoard.getSquares());
        // Check placement along y-axis
        actualBoard.placeBattleShip(1, 2, ship2, false);
        for (int y = 2; y < 2 + ship2.getLength(); y++) {
            expectedSquares[1][y].registerShip();
        }
        testSquares(expectedSquares, actualBoard.getSquares());
    }

    @Test
    void testValidateCoordinates() {
        // Testing x/y above/over boardSize and all (we think) possible combinations
        for (Integer key : xToYCoordinatesMap.keySet()) {
            assertThrows(IllegalArgumentException.class,
                    () -> actualBoard.validateCoordinates(key, xToYCoordinatesMap.get(key)));
        }
    }

    @Test
    void testFireShot() {
        // Out of bonds exceptions test
        for (Integer key : xToYCoordinatesMap.keySet()) {
            assertThrows(IllegalArgumentException.class,
                    () -> actualBoard.fireShot(key, xToYCoordinatesMap.get(key)));
        }
        // Test shots are registered
        actualBoard.fireShot(0, 0);
        actualBoard.fireShot(1, 0);
        expectedSquares[0][0].registerHit();
        expectedSquares[1][0].registerHit();
        assertEquals(true, actualBoard.getSquare(0, 0).getIsHit());
        assertEquals(true, actualBoard.getSquare(1, 0).getIsHit());
        // Test that firing at the same pos several times throes state exception
        assertThrows(IllegalStateException.class, () -> actualBoard.fireShot(1, 0),
                "Test that state exception thrown when we try to hit position that is already hit");
        testSquares(expectedSquares, actualBoard.getSquares());
    }

    @Test
    void testIsGameOver() {
        List<int[]> positions = generateShips();
        for (int[] pos : positions) {
            assertFalse(actualBoard.isGameOver());
            actualBoard.fireShot(pos[0], pos[1]);
        }
        assertTrue(actualBoard.isGameOver());
    }

    /**
     * Tests placeAllBattleships() method. Here we test if there is expected amounts
     * of squares which contains ship after placing all boats. We do not test for
     * placement of the ships, however placeAllShips() relies on placeBattleShip()
     * for actually placing the ships. placeBattleShip() method has tests which
     * might justify the lack of placement testing in this very test method.
     */
    @Test
    void testPlaceAllBattleships() {
        List<Battleship> ships = new ArrayList<>();
        ships.add(new Battleship(4));
        ships.add(new Battleship(3));
        ships.add(new Battleship(2));
        int expectedTilesWithShip = 9;

        GameBoard board = new GameBoard(6, 6);
        int actualTilesWithShip = 0;
        // We test the method 50 times to properly test the method since it randomly
        // places boats
        for (int i = 0; i < 50; i++) {
            actualTilesWithShip = 0;
            board.placeAllBattleships(ships);
            for (int x = 0; x < board.getSquares().length; x++) {
                for (int y = 0; y < board.getSquares()[x].length; y++) {
                    if (board.getSquares()[x][y].getContainsShip()) {
                        actualTilesWithShip++;
                    }
                }
            }
            assertEquals(expectedTilesWithShip, actualTilesWithShip);
        }
    }

    @Test
    void testResetBoard() {
        generateShips();
        actualBoard.emptyBoard();
        boolean boardIsReset = true;
        for (int x = 0; x < actualBoard.getSquares().length; x++) {
            for (int y = 0; y < actualBoard.getSquares()[x].length; y++) {
                Position pos = actualBoard.getSquares()[x][y];
                if (pos.getContainsShip() == true || pos.getIsHit() == true || pos.getX() != x || pos.getY() != y) {
                    boardIsReset = false;
                }
            }
        }
        assertTrue(boardIsReset);
    }

    @Test
    void testSerialize() {
        GameBoard board = new GameBoard(4, 4);
        board.getSquares()[0][3].registerShip();
        board.getSquares()[1][3].registerShip();
        board.getSquares()[2][3].registerShip();
        board.getSquares()[3][3].registerShip();
        board.getSquares()[0][0].registerHit();
        board.getSquares()[0][3].registerHit();
        String expected = "0:0:true:false-0:1:false:false-0:2:false:false-0:3:true:true-";
        expected += "1:0:false:false-1:1:false:false-1:2:false:false-1:3:false:true-";
        expected += "2:0:false:false-2:1:false:false-2:2:false:false-2:3:false:true-";
        expected += "3:0:false:false-3:1:false:false-3:2:false:false-3:3:false:true-";

        assertEquals(expected, board.serialize());

    }

    @Test
    void testEquals() {
        GameBoard board1 = new GameBoard(5, 5);
        GameBoard board2 = new GameBoard(5, 5);
        assertEquals(board1, board2);
        board2 = new GameBoard(6, 6);
        assertNotEquals(board1, board2);
        board2 = new GameBoard(4, 4);
        assertNotEquals(board1, board2);
        board2 = new GameBoard(5, 5);
        board1.fireShot(3, 4);
        assertNotEquals(board1, board2);
        board2.fireShot(2, 4);
        assertNotEquals(board1, board2);
        board1.fireShot(2, 4);
        assertNotEquals(board1, board2);
        board2.fireShot(3, 4);
        assertEquals(board1, board2);
        board1.placeBattleShip(0, 0, ship1, true);
        assertNotEquals(board1, board2);
        board2.placeBattleShip(0, 0, ship1, true);
        assertEquals(board1, board2);
        board2.placeBattleShip(0, 1, ship2, false);
        assertNotEquals(board1, board2);
        board1.placeBattleShip(0, 1, ship2, false);
        assertEquals(board1, board2);
        board2 = null;
        assertNotEquals(board1, board2);
        assertNotEquals(board1, ship1);
    }
}
