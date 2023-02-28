package battleships.models;

/**
 * Holds positive coordinates
 */
public class Position {
    private final int x;
    private final int y;
    private boolean isHit;
    private boolean containsShip;

    /**
     * @param x x-coordinate, must be positive
     * @param y y-coordinate, must be positive
     * @throws IllegalArgumentException throws when x or y is negative
     */
    public Position(int x, int y) throws IllegalArgumentException {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Cannot have negative coordinates");
        }

        this.x = x;
        this.y = y;
        this.isHit = false;
        this.containsShip = false;
    }

    public void registerHit() throws IllegalStateException {
        if (this.isHit) {
            throw new IllegalStateException(String.format("Position is already hit at (%d, %d)", this.x, this.y));
        }
        this.isHit = true;
    }

    public void registerShip() throws IllegalStateException {
        if (this.containsShip)
            throw new IllegalStateException(
                    String.format("A ship is already registered in this position at (%d, %d)", this.x,
                            this.y));
        this.containsShip = true;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean getIsHit() {
        return this.isHit;
    }

    public boolean getContainsShip() {
        return this.containsShip;
    }

    @Override
    public String toString() {
        return String.format("pos:(%d, %d) hit:%s ship:%s", this.x, this.y, this.isHit, this.containsShip);
    }

    public String serialize() {
        return String.format("%d:%d:%s:%s", this.x, this.y, this.isHit, this.containsShip);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof Position))
            return false;

        Position pos = (Position) o;

        return this.x == pos.x && this.y == pos.y &&
                this.isHit == pos.isHit && this.containsShip == pos.containsShip;
    }

    public static void main(String[] args) {
        Position pos = new Position(1, 2);
        System.out.println(pos);
    }
}
