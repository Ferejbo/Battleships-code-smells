package battleships.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameTest {

    private Game game;
    private int width = 6;
    private int height = 6;

    @BeforeEach
    void init() {
        game = new Game(width, height);
    }

    @Test
    void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new Game(4, 7),
                "Board width and height not equal exception");
        assertThrows(IllegalArgumentException.class, () -> new Game(3, 3),
                "Board width and height smaller than minimum");

        List<Battleship> battleships = new ArrayList<>();
        battleships.add(new Battleship(4));
        battleships.add(new Battleship(3));
        battleships.add(new Battleship(2));
        int width = 6;
        int height = 6;
        GameBoard gameBoard1 = new GameBoard(width, height);
        GameBoard gameBoard2 = new GameBoard(width, height);
        Player player1 = new Player("Player 1", gameBoard1, gameBoard2);
        Player player2 = new Player("Player 2", gameBoard2, gameBoard1);
        Game game = new Game(width, height);

        assertTrue(player1.valueEquals(game.getPlayer1()));
        assertTrue(player2.valueEquals(game.getPlayer2()));
        assertEquals(game.getPlayer1(), game.getCurrentPlayer());
        assertTrue(game.getIsPlacementPhase());
        assertEquals(width, game.getBoardWidth());
        assertEquals(height, game.getBoardHeight());

        for (int i = 0; i < battleships.size(); i++) {
            assertEquals(battleships.get(i).getLength(), game.getBattleships().get(i).getLength());
        }

        game = new Game(player1, player2, player2, width, height);
        assertEquals(player1, game.getPlayer1());
        assertEquals(player2, game.getPlayer2());
        assertEquals(game.getPlayer2(), game.getCurrentPlayer());
        assertFalse(game.getIsPlacementPhase());
        assertEquals(width, game.getBoardWidth());
        assertEquals(height, game.getBoardHeight());
    }

    @Test
    void testSwitchPlayer() {
        game.switchPlayer();
        assertEquals(game.getPlayer2(), game.getCurrentPlayer());
        game.getCurrentPlayer().setShots(game.getCurrentPlayer().getMaxShots() - 1);

        game.switchPlayer();
        assertEquals(game.getPlayer1(), game.getCurrentPlayer());
        assertEquals(game.getCurrentPlayer().getMaxShots() - 1, game.getPlayer2().getShotsLeft());
        game.endPlacementPhase();
        assertFalse(game.getIsPlacementPhase());

        game.switchPlayer();
        assertEquals(game.getPlayer2(), game.getCurrentPlayer());

        game.switchPlayer();
        assertEquals(game.getCurrentPlayer().getMaxShots(), game.getPlayer2().getShotsLeft());
        assertEquals(game.getPlayer1(), game.getCurrentPlayer());
    }

    void testShipTileAmount(GameBoard board, int expectedAmount) {
        Position[][] squares = board.getSquares();
        int actualTilesWithShip = 0;
        for (int x = 0; x < squares.length; x++) {
            for (int y = 0; y < squares[x].length; y++) {
                if (squares[x][y].getContainsShip())
                    actualTilesWithShip++;
            }
        }
        assertEquals(expectedAmount, actualTilesWithShip);
    }

    @Test
    void testRandomizeCurrentBoard() {
        int expectedTilesWithShip = 0;
        for (Battleship ship : game.getBattleships()) {
            expectedTilesWithShip += ship.getLength();
        }
        game.randomizeCurrentBoard();
        testShipTileAmount(game.getPlayer1().getFriendlyBoard(), expectedTilesWithShip);
        game.submitBoard("Hei");
        game.randomizeCurrentBoard();
        testShipTileAmount(game.getPlayer2().getFriendlyBoard(), expectedTilesWithShip);
        game.endPlacementPhase();
        assertThrows(IllegalStateException.class, () -> game.randomizeCurrentBoard(),
                "Randomize board after placement phase exception");
    }

    @Test
    void testSubmitBoard() {
        assertThrows(IllegalArgumentException.class, () -> game.submitBoard(""),
                "Empty name exception");
        assertThrows(IllegalArgumentException.class, () -> game.submitBoard("   "),
                "Only whitespace name exception");
        game.submitBoard("Gunnleif");
        assertEquals("Gunnleif", game.getPlayer1().getName());
        assertEquals(game.getPlayer2(), game.getCurrentPlayer());
        game.submitBoard("Gunnhild");
        assertEquals("Gunnhild", game.getPlayer2().getName());
        assertEquals(game.getPlayer1(), game.getCurrentPlayer());
        assertFalse(game.getIsPlacementPhase());
        assertThrows(IllegalStateException.class, () -> game.submitBoard("Kåre"),
                "Submit board after placement phase exception");
    }

    void testFireShotsForPlayer(Player player) {
        GameBoard boardToBeFired = player.getEnemyBoard();
        boardToBeFired.getSquare(2, 2).registerShip();
        boardToBeFired.getSquare(3, 2).registerShip();
        boardToBeFired.getSquare(4, 2).registerShip();

        assertFalse(game.fireShot(2, 2));
        assertTrue(boardToBeFired.getSquare(2, 2).getIsHit());
        assertFalse(game.fireShot(2, 4));
        assertTrue(boardToBeFired.getSquare(2, 4).getIsHit());
        assertFalse(game.fireShot(3, 2));
        assertTrue(boardToBeFired.getSquare(3, 2).getIsHit());
        assertThrows(IllegalStateException.class, () -> assertTrue(game.fireShot(4, 2)),
                "Out of bullets exception");
        player.fillShots();
        assertThrows(IllegalStateException.class, () -> assertTrue(game.fireShot(3, 2)),
                "Cannot shoot already shot position exception");
        assertThrows(IllegalArgumentException.class, () -> assertTrue(game.fireShot(-1, 7)),
                "Out of bounds coordinates exception");
        assertEquals(player.getMaxShots(), player.getShotsLeft());
        // GAME OVER check
        assertTrue(game.fireShot(4, 2));
    }

    @Test
    void testFireShot() {
        assertThrows(IllegalStateException.class, () -> assertTrue(game.fireShot(3, 2)),
                "Cannot shoot during placement phase exception");
        game.endPlacementPhase();
        testFireShotsForPlayer(game.getCurrentPlayer());
        game.switchPlayer();
        testFireShotsForPlayer(game.getCurrentPlayer());
    }

    @Test
    void testSerialize() {
        Game game = new Game(4, 4);
        GameBoard player1Board = game.getPlayer1().getFriendlyBoard();
        GameBoard player2Board = game.getPlayer2().getFriendlyBoard();
        player1Board.getSquare(2, 2).registerShip();
        player1Board.getSquare(3, 2).registerShip();
        player2Board.getSquare(1, 1).registerShip();
        player2Board.getSquare(3, 1).registerShip();

        player1Board.getSquare(2, 2).registerHit();
        player1Board.getSquare(3, 3).registerHit();
        player1Board.getSquare(1, 2).registerHit();
        player2Board.getSquare(2, 2).registerHit();

        game.getPlayer1().setName("Kåre");
        game.getPlayer2().setName("Gunnar");

        game.switchPlayer();

        String expected = "4;4;\n";
        expected += player1Board.serialize() + ";\n";
        expected += player2Board.serialize() + ";\n";
        expected += "Kåre;3;false;Gunnar;3;true;";

        assertEquals(expected, game.serialize());
    }

    void testEqualsPerPlayer(Player player) {
        Game game1 = new Game(6, 6);
        Game game2 = new Game(6, 6);

    }

    @Test
    void testEquals() {
        Game game1 = new Game(width, height);
        Game game2 = new Game(5, 5);
        assertNotEquals(game1, game2);
        game2 = new Game(width, height);
        assertEquals(game1, game2);
        game2.getPlayer1().setName("Kåre");
        assertNotEquals(game1, game2);
        game1.getPlayer1().setName("Kåre");
        assertEquals(game1, game2);
        game1.switchPlayer();
        assertNotEquals(game1, game2);
        game2.switchPlayer();
        assertEquals(game1, game2);
        game1.endPlacementPhase();
        assertNotEquals(game1, game2);
        game2.endPlacementPhase();
        assertEquals(game1, game2);
        game1.getPlayer2().setName("Gunnar");
        assertNotEquals(game1, game2);
        game2.getPlayer2().setName("Gunnar");
        assertEquals(game1, game2);
        game1.getCurrentPlayer().getEnemyBoard().getSquare(2, 2).registerShip();
        assertNotEquals(game1, game2);
        game2.getCurrentPlayer().getEnemyBoard().getSquare(2, 2).registerShip();
        assertEquals(game1, game2);
        game1.getCurrentPlayer().getEnemyBoard().getSquare(1, 0).registerHit();
        assertNotEquals(game1, game2);
        game2.getCurrentPlayer().getEnemyBoard().getSquare(1, 1).registerHit();
        assertNotEquals(game1, game2);
        game2.getCurrentPlayer().getEnemyBoard().getSquare(1, 0).registerHit();
        game1.getCurrentPlayer().getEnemyBoard().getSquare(1, 1).registerHit();
        assertEquals(game1, game2);
    }
}
