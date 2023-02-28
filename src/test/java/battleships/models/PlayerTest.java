package battleships.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerTest {

    private GameBoard friendlyBoard;
    private GameBoard enemyBoard;
    private int boardWidth;
    private int boardHeight;
    private Player player;

    @BeforeEach
    void init() {
        boardWidth = 4;
        boardHeight = 4;
        friendlyBoard = new GameBoard(boardWidth, boardHeight);
        enemyBoard = new GameBoard(boardWidth, boardHeight);
        player = new Player("Kåre", friendlyBoard, enemyBoard);
    }

    @Test
    void testConstructor() {
        assertEquals("Kåre", player.getName());
        assertEquals(friendlyBoard, player.getFriendlyBoard());
        assertEquals(enemyBoard, player.getEnemyBoard());
        assertEquals(player.getMaxShots(), player.getShotsLeft());

        assertThrows(IllegalArgumentException.class, () -> new Player("", friendlyBoard, enemyBoard),
                "Tests empty name string");
        assertThrows(IllegalArgumentException.class, () -> new Player("     ", friendlyBoard, enemyBoard),
                "Tests whitespace name string");
    }

    @Test
    void testSetName() {
        player.setName("Gunnar");
        assertEquals("Gunnar", player.getName());
        assertThrows(IllegalArgumentException.class, () -> player.setName(""),
                "Tests empty name string");
        assertThrows(IllegalArgumentException.class, () -> player.setName("   "),
                "Tests whitespace name string");
    }

    @Test
    void testShots() {
        assertThrows(IllegalArgumentException.class, () -> player.setShots(-1),
                "negative shots exception");
        assertThrows(IllegalArgumentException.class, () -> player.setShots(player.getMaxShots() + 1),
                "too many shots exception");
        player.setShots(player.getMaxShots() - 1);
        assertEquals(player.getMaxShots() - 1, player.getShotsLeft());
        player.fillShots();
        assertEquals(player.getMaxShots(), player.getShotsLeft());
    }

    @Test
    void testFireShots() {
        assertThrows(IllegalArgumentException.class, () -> player.fireShot(boardWidth, boardHeight),
                "Coordinates out of bounds exception");
        assertThrows(IllegalArgumentException.class, () -> player.fireShot(-2, -1),
                "Coordinates out of bounds exception");
        for (int i = 0; i < player.getMaxShots(); i++) {
            player.fireShot(0, i);
        }
        assertThrows(IllegalStateException.class, () -> player.fireShot(2, 2),
                "Out of bullets exception");
        player.fillShots();
        player.fireShot(2, 2);
        assertEquals(player.getMaxShots() - 1, player.getShotsLeft());
        assertTrue(player.getEnemyBoard().getSquare(2, 2).getIsHit());
        assertThrows(IllegalStateException.class, () -> player.fireShot(2, 2),
                "Shoot same position twice exception");
    }

    @Test
    void testEquals() {
        Player player2 = new Player("Gunnar", friendlyBoard, enemyBoard);
        assertNotEquals(player, player2);
        player2 = new Player("Kåre", enemyBoard, friendlyBoard);
        assertNotEquals(player, player2);
        player2 = new Player("Kåre", friendlyBoard, enemyBoard);
        assertEquals(player, player2);
    }

    @Test
    void testValueEquals() {
        Player player2 = new Player("Gunnar", friendlyBoard, enemyBoard);
        assertFalse(player.valueEquals(player2));
        player2 = new Player("Kåre", enemyBoard, friendlyBoard);
        assertTrue(player.valueEquals(player2));
        player2 = new Player("Kåre", friendlyBoard, enemyBoard);
        assertTrue(player.valueEquals(player2));
    }
}
