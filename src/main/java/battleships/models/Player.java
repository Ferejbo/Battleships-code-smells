package battleships.models;

public class Player {
    private final int maxShots = 3;

    private String name;
    private final GameBoard friendlyBoard;
    private final GameBoard enemyBoard;
    private int shotsLeft;

    public Player(String name, GameBoard friendlyBoard, GameBoard enemyBoard) throws IllegalArgumentException {
        validateName(name);
        this.name = name;
        this.friendlyBoard = friendlyBoard;
        this.enemyBoard = enemyBoard;
        this.shotsLeft = maxShots;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    private void validateName(String name) throws IllegalArgumentException {
        boolean throwException = true;
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) != ' ') {
                throwException = false;
            }
        }
        if (throwException) {
            throw new IllegalArgumentException("New name must contain at least one non whitespace character");
        }
    }

    public void fireShot(int x, int y) throws IllegalStateException, IllegalArgumentException {
        if (shotsLeft < 1) {
            throw new IllegalStateException("No more shots left");
        }
        enemyBoard.fireShot(x, y);
        shotsLeft--;
    }

    public void fillShots() {
        shotsLeft = 3;
    }

    public void setShots(int shots) throws IllegalArgumentException {
        if (shots < 0) {
            throw new IllegalArgumentException("Player cannot have negative amounts of shots");
        }
        if (shots > maxShots) {
            throw new IllegalArgumentException(
                    String.format("Player is not allowed to have more than %d shots", maxShots));
        }
        this.shotsLeft = shots;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof Player))
            return false;

        Player player = (Player) o;

        return this.name.equals(player.name) && this.friendlyBoard == player.friendlyBoard
                && this.enemyBoard == player.enemyBoard && this.shotsLeft == player.shotsLeft;
    }

    /**
     * Same as equals() method, but with compare of the boards using their equals()
     * method isntead of compare by reference
     * 
     * @param player Player object to compare to this
     * @return whether player and this are equal
     */
    public boolean valueEquals(Player player) {
        return this.name.equals(player.name) && this.friendlyBoard.equals(friendlyBoard)
                && this.enemyBoard.equals(player.enemyBoard) && this.shotsLeft == player.shotsLeft;
    }

    public int getShotsLeft() {
        return shotsLeft;
    }

    public GameBoard getFriendlyBoard() {
        return friendlyBoard;
    }

    public GameBoard getEnemyBoard() {
        return enemyBoard;
    }

    public String getName() {
        return name;
    }

    public int getMaxShots() {
        return maxShots;
    }
}
