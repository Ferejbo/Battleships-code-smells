package battleships.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import battleships.models.Position;
import javafx.geometry.Pos;

public class PositionTest {

    @BeforeEach
    void setup() {

    }

    /**
     * Assert that constructor throws IllegalArgumentException
     * 
     * @param x x-coordinate
     * @param y y-coordinate
     */
    private void assertConstructorThrowsException(int x, int y) {
        assertThrows(IllegalArgumentException.class, () -> {
            new Position(x, y);
        });
    }

    @Test
    void testConstructor() {
        // Exception checks (negative coordinates)
        assertConstructorThrowsException(-1, 0);
        assertConstructorThrowsException(0, -2);
        assertConstructorThrowsException(-3, -2);

        // Checking that values initiatlized are as expected
        Position pos = new Position(2, 3);
        assertEquals(2, pos.getX());
        assertEquals(3, pos.getY());
        assertEquals(false, pos.getIsHit());
        assertEquals(false, pos.getContainsShip());
    }

    @Test
    void testRegisterHitAndShip() {
        Position pos = new Position(1, 2);
        assertEquals(false, pos.getIsHit());

        // test register hit is registered
        pos.registerHit();
        assertEquals(true, pos.getIsHit());
        assertEquals(false, pos.getContainsShip());
        // test excesive hit throws state exception
        assertThrows(IllegalStateException.class, () -> {
            pos.registerHit();
        });

        // test register ship is registered
        pos.registerShip();
        assertEquals(true, pos.getContainsShip());
        // test excesive register ship throws state exception
        assertThrows(IllegalStateException.class, () -> {
            pos.registerShip();
        });
    }

    @Test
    void testSerialize() {
        Position pos = new Position(3, 4);
        assertEquals("3:4:false:false", pos.serialize());
        pos.registerHit();
        assertEquals("3:4:true:false", pos.serialize());
        pos.registerShip();
        assertEquals("3:4:true:true", pos.serialize());
        pos = new Position(0, 0);
        pos.registerShip();
        assertEquals("0:0:false:true", pos.serialize());
    }

    @Test
    void testEquals() {
        Position pos1 = new Position(3, 3);
        Position pos2 = new Position(3, 3);
        assertEquals(pos1, pos2);
        pos1.registerHit();
        assertNotEquals(pos1, pos2);
        pos2.registerHit();
        assertEquals(pos1, pos2);
        pos1.registerShip();
        assertNotEquals(pos1, pos2);
        pos2.registerShip();
        assertEquals(pos1, pos2);

        pos1 = new Position(0, 3);
        pos2 = new Position(3, 3);
        assertNotEquals(pos1, pos2);
        pos2 = new Position(0, 2);
        assertNotEquals(pos1, pos2);
        pos2 = new Position(3, 0);
        assertNotEquals(pos1, pos2);
        pos1 = new Position(3, 0);
        assertEquals(pos1, pos2);
        pos2 = null;
        assertNotEquals(pos1, pos2);
        Battleship ship = new Battleship(3);
        assertNotEquals(pos1, ship);
    }
}
