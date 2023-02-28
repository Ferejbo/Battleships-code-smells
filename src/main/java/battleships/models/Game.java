package battleships.models;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final Player player1;
    private final Player player2;
    private Player currentPlayer;

    private final int boardWidth;
    private final int boardHeight;

    private boolean isPlacementPhase;

    private List<Battleship> battleships;

    public Game(int boardWidth, int boardHeight) throws IllegalArgumentException {
        GameBoard gameBoard1 = new GameBoard(boardWidth, boardHeight);
        GameBoard gameBoard2 = new GameBoard(boardWidth, boardHeight);
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        player1 = new Player("Player 1", gameBoard1, gameBoard2);
        player2 = new Player("Player 2", gameBoard2, gameBoard1);
        currentPlayer = player1;

        isPlacementPhase = true;

        battleships = new ArrayList<>();
        battleships.add(new Battleship(4));
        battleships.add(new Battleship(3));
        battleships.add(new Battleship(2));
    }

    // Constructor used to initiate game after reading from file
    public Game(Player player1, Player player2, Player currentPlayer, int boardWidth, int boardHeight) {
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = currentPlayer;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.isPlacementPhase = false;
    }

    public void randomizeCurrentBoard() throws IllegalStateException {
        if (!isPlacementPhase)
            throw new IllegalStateException("Cannot place battleships after placement phase");
        currentPlayer.getFriendlyBoard().placeAllBattleships(battleships);
    }

    public void switchPlayer() {
        currentPlayer = currentPlayer == player1 ? player2 : player1;
        if (!isPlacementPhase)
            currentPlayer.fillShots();
    }

    /**
     * Sets new name for player, switches player and if next player is player1, we
     * end the placement phase
     * 
     * @param newName New name for the player that submitted board
     */
    public void submitBoard(String newName) throws IllegalArgumentException, IllegalStateException {
        if (!isPlacementPhase)
            throw new IllegalStateException("Cannot submit board after placement phase");
        currentPlayer.setName(newName);
        switchPlayer();
        if (currentPlayer == player1) {
            endPlacementPhase();
        }
    }

    /**
     * Lets current player fire shot. Then returns boolean if the game is over
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @return Whether the game is over after the shot
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     */
    public boolean fireShot(int x, int y) throws IllegalArgumentException, IllegalStateException {
        if (isPlacementPhase) {
            throw new IllegalStateException("Cannot shoot enemy board during placement phase:/");
        }
        currentPlayer.fireShot(x, y);
        return currentPlayer.getEnemyBoard().isGameOver();
    }

    public void endPlacementPhase() {
        isPlacementPhase = false;
    }

    public String serialize() {
        String result;
        result = String.format("%d;%d;\n", boardWidth, boardHeight);
        result += String.format("%s;\n", player1.getFriendlyBoard().serialize());
        result += String.format("%s;\n", player2.getFriendlyBoard().serialize());
        boolean player1IsCurrentPlayer = currentPlayer == player1;
        result += String.format("%s;%d;%s;%s;%d;%s;", player1.getName(), player1.getShotsLeft(), player1IsCurrentPlayer,
                player2.getName(), player2.getShotsLeft(), !player1IsCurrentPlayer);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Game))
            return false;

        Game game = (Game) o;

        return player1.valueEquals(game.player1) && player2.valueEquals(game.player2)
                && currentPlayer.valueEquals(game.currentPlayer) && isPlacementPhase == game.isPlacementPhase;
    }

    public Player getPlayer1() {
        return this.player1;
    }

    public Player getPlayer2() {
        return this.player2;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public int getBoardWidth() {
        return this.boardWidth;
    }

    public int getBoardHeight() {
        return this.boardHeight;
    }

    public boolean getIsPlacementPhase() {
        return this.isPlacementPhase;
    }

    public List<Battleship> getBattleships() {
        return battleships;
    }

}
