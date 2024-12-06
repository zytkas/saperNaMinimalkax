/**
 * @author Danylo Zhdanov 68514 and Gilhereme Santos 65443
 * Represents a game where multiple players navigate through a grid searching for a crystal
 * while avoiding mines and collecting shields. This class implements a turn-based game system
 * with state management for multiple players and game conditions.
 * The game follows these core rules:
 * - Players move on a grid containing mines, shields, and a crystal
 * - Each player can move, detect mines, or skip their turn
 * - Players can be eliminated by mines unless protected by shields
 * - The game ends when either:
 *   - A player finds the crystal
 *   - Only one player remains active
 *   - All players are eliminated
 *
 * @see Grid
 * @see Player
 * @see Position
 */

public class Game {

    public static final int MOVE_SUCCESS = 0;
    public static final int MOVE_OUT_OF_BOUNDS = 1;
    public static final int MOVE_POSITION_OCCUPIED = 2;
    public static final int MOVE_MINE_HIT = 3;
    public static final int MOVE_SHIELD_PICKUP = 4;
    public static final int MOVE_CRYSTAL_FOUND = 5;
    public static final int MOVE_SUCCESS_PROTECTED = 6;
    public static final char MINE_CELL = 'M';
    public static final char MIN_SHIELD_CELL = '1';
    public static final char MAX_SHIELD_CELL = '9';
    public static final char CRYSTAL_CELL = 'X';
    public static final char EMPTY_CELL = '.';

    private final Grid grid;
    private Player[] players;
    private int currentPlayerIndex;
    private int allPlayers;
    private int activePlayers;
    private boolean isGameOver;
    private int lastMoveRow;
    private int lastMoveCol;
    private int lastShieldDuration;
    private String lastPlayerName;

    public Game(int rows, int cols, char[][] gridData) {
        isGameOver = false;
        currentPlayerIndex = 0;
        this.grid = new Grid(rows, cols);
        for (int i = 0; i < rows; i++) {
            grid.loadRow(i, gridData[i]);
        }
    }

    /**
     * Initializes the player array with the specified number of slots.
     * Must be called before adding any players to the game.
     *
     * @param numPlayers The maximum number of players that can join the game
     */
    public void initializePlayers(int numPlayers) {
        players = new Player[numPlayers];
        activePlayers = 0;
        allPlayers = 0;
    }

    /**
     * Attempts to add a new player to the game at the specified position.
     * The position must be valid, empty, and not occupied by another player.
     *
     * @param row The row coordinate for the new player
     * @param col The column coordinate for the new player
     * @param name The name of the new player
     * @return true if the player was successfully added, false otherwise
     */
    public boolean addPlayer(int row, int col, String name) {
        Position pos = new Position(row, col);
        if (grid.isValidPosition(pos) && grid.isEmpty(pos) && !isPositionTaken(pos)) {
            players[activePlayers] = new Player(name, row, col);
            activePlayers++;
            allPlayers++;
            return true;
        }
        return false;
    }

    /**
     * Checks if a position is currently occupied by any active player.
     *
     * @param pos The position to check
     * @return true if the position is occupied by an active player, false otherwise
     */
    private boolean isPositionTaken(Position pos) {
        for (int i = 0; i < allPlayers; i++) {
            if (!players[i].isEliminated() &&
                    players[i].getPosition().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Processes a move for the current player in the specified direction. Handles moving to
     * empty cells, out of bounds attempts, occupied positions, mines, shields, and crystal.
     *
     * @param direction The direction to move ("up", "down", "left", "right")
     * @return An integer constant indicating the move result
     */
    public int movePlayer(String direction) {
        Player player = getCurrentPlayer();
        Position newPosition = player.getPosition().calculateNewPosition(direction);
        if (!grid.isValidPosition(newPosition)) {
            lastPlayerName = player.getName();
            player.finishTurn();
            nextTurn();
            return MOVE_OUT_OF_BOUNDS;
        }
        if (isPositionTaken(newPosition)) {
            lastPlayerName = player.getName();
            player.finishTurn();
            nextTurn();
            return MOVE_POSITION_OCCUPIED;
        }
        char cell = grid.getCell(newPosition);
        int result = processCell(player, newPosition, cell);
        int row = player.getPosition().getRow();
        int col = player.getPosition().getColumn();
        lastPlayerName = player.getName();
        player.finishTurn();
        if (!isGameOver) {
            nextTurn();
        }
        lastMoveRow = row;
        lastMoveCol = col;
        lastShieldDuration = player.getShieldDuration();
        return result;
    }

    /**
     * Processes the interaction between a player and a cell on the grid.
     * Handles special cell types (mine, shield, crystal) and their effects.
     *
     * @param player The player moving to the cell
     * @param newPosition The position of the cell
     * @param cell The type of cell (represented by a char)
     * @return An integer constant indicating the result of the interaction
     */
    private int processCell(Player player, Position newPosition, char cell) {
        if (cell == MINE_CELL) {
            if (player.isProtected()) {
                grid.clearCell(newPosition);
                player.moveTo(newPosition);
                return MOVE_SUCCESS_PROTECTED;
            } else {
                eliminatePlayer(player);
                grid.clearCell(newPosition);
                player.moveTo(newPosition);
                return MOVE_MINE_HIT;
            }
        } else if (cell >= MIN_SHIELD_CELL && cell <= MAX_SHIELD_CELL) {
            player.addShield(Character.getNumericValue(cell));
            grid.clearCell(newPosition);
            player.moveTo(newPosition);
            return MOVE_SHIELD_PICKUP;
        } else if (cell == CRYSTAL_CELL) {
            isGameOver = true;
            player.collectCrystal();
            player.moveTo(newPosition);
            return MOVE_CRYSTAL_FOUND;
        }
        player.moveTo(newPosition);
        return MOVE_SUCCESS;
    }

    /**
     * Eliminates a player from the game and checks if this triggers game over.
     * Game over occurs if only one player remains active after elimination.
     *
     * @param player The player to eliminate
     */
    private void eliminatePlayer(Player player) {
        player.eliminate();
        activePlayers--;

        if (activePlayers == 1) {
            isGameOver = true;
        }
    }

    /**
     * Allows the current player to detect mines in adjacent cells.
     * Counts the number of mines in the eight cells surrounding the player's position.
     *
     * @return The number of mines in adjacent cells
     */
    public int detect() {
        int mines = grid.countSurroundingMines(getCurrentPlayer().getPosition());
        getCurrentPlayer().finishTurn();
        nextTurn();
        return mines;
    }

    /**
     * Allows the current player to skip their turn.
     * Advances the game to the next player's turn.
     */
    public void skip() {
        getCurrentPlayer().finishTurn();
        nextTurn();
    }

    /**
     * Creates and returns an iterator over players sorted by their ranking.
     * Players are ranked based on the following criteria (in order):
     * 1. Crystal possession (player with crystal ranks highest)
     * 2. Elimination status (active players rank higher than eliminated)
     * 3. For eliminated players: number of moves (more moves rank higher)
     * 4. For active players: distance to crystal (closer ranks higher)
     * 5. Alphabetical order of names (as tiebreaker)</p>
     *
     * @return An iterator over the sorted player array
     */
    @SuppressWarnings("ManualArrayCopy")
    public MyIterator getRankedPlayers() {
        Player[] rankedPlayers = new Player[allPlayers];
        for (int i = 0; i < allPlayers; i++) {
            rankedPlayers[i] = players[i];
        }

        for (int i = 0; i < rankedPlayers.length - 1; i++) {
            for (int j = 0; j < rankedPlayers.length - i - 1; j++) {
                Player p1 = rankedPlayers[j];
                Player p2 = rankedPlayers[j + 1];

                if (shouldSwapPlayers(p1, p2)) {
                    Player temp = rankedPlayers[j];
                    rankedPlayers[j] = rankedPlayers[j + 1];
                    rankedPlayers[j + 1] = temp;
                }
            }
        }
        return new MyIterator(rankedPlayers);
    }

    /**
     * Determines if two players should swap positions in the ranking.
     * Implements the ranking criteria logic described in getRankedPlayers.
     *
     * @param p1 The first player to compare
     * @param p2 The second player to compare
     * @return true if the players should swap positions, false otherwise
     */
    private boolean shouldSwapPlayers(Player p1, Player p2) {
        // Firstly checks if player has collected crystal
        if (p1.hasCollectedCrystal() != p2.hasCollectedCrystal()) {
            return !p1.hasCollectedCrystal(); // Player with crystal should be 1'st
        }
        // Check status  eliminated
        if (p1.isEliminated() != p2.isEliminated()) {
            return p1.isEliminated(); // Eliminated players go last
        }
        // If both eliminated, compare by number of moves
        if (p1.isEliminated()) {
            if (p1.getTotalMoves() != p2.getTotalMoves()) {
                return p1.getTotalMoves() < p2.getTotalMoves(); // More moves = higher on list
            }
            // Equal number of moves, compare by name
            return p1.getName().compareTo(p2.getName()) > 0;
        }
        //If both active, compare by distance to crystal
        int dist1 = grid.getDistanceToCrystal(p1.getPosition());
        int dist2 = grid.getDistanceToCrystal(p2.getPosition());
        if (dist1 != dist2) {
            return dist1 > dist2; // Less distance = higher on list
        }
        // Equal distance, compare by name
        return p1.getName().compareTo(p2.getName()) > 0;
    }

    /**
     * Advances the game to the next active player's turn.
     * Skips eliminated players and can trigger game over if no valid moves remain.
     */
    private void nextTurn() {
        if (isGameOver) return;
        int startingIndex = currentPlayerIndex;
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % allPlayers;
            if (currentPlayerIndex == startingIndex) {
                isGameOver = true;
                return;
            }
        } while (players[currentPlayerIndex].isEliminated());
    }

    /**
     * Determines and returns the winner of the game.
     * Winners are determined in the following order:
     * 1. Player who collected the crystal
     * 2. Last surviving player
     * 3. First available player (fallback)
     *
     * @return The winning Player object
     */
    public String getWinner() {
        if (isGameOver) {
            Player winner = getWinnerWithCrystal();
            if (winner != null) {

                return winner.getName();
            }
            winner = getLastSurvivingPlayer();
            if (winner != null) {
                return winner.getName();
            }
        }
        return getFirstAvailablePlayer().getName();
    }

    /**
     * Finds the player who has collected the crystal, if any.
     *
     * @return The Player who has the crystal, or null if none
     */
    private Player getWinnerWithCrystal() {
        for (int i = 0; i < allPlayers; i++) {
            Player player = players[i];
            if (player != null && player.hasCollectedCrystal()) {
                return player;
            }
        }
        return null;
    }

    /**
     * Finds the last non-eliminated player, if any.
     *
     * @return The last surviving Player, or null if none
     */
    private Player getLastSurvivingPlayer() {
        for (int i = 0; i < allPlayers; i++) {
            Player player = players[i];
            if (player != null && !player.isEliminated()) {
                return player;
            }
        }
        return null;
    }

    /**
     * Returns the first non-null player in the array.
     * This is a fallback method that should rarely be needed.
     *
     * @return The first available Player object
     */
    private Player getFirstAvailablePlayer() {
        for (int i = 0; i < allPlayers; i++) {
            if (players[i] != null) {
                return players[i];
            }
        }
        return players[0]; // Fallback, should never reach this point
    }

    /**
     * Returns the first non-null player in the array.
     * This is a fallback method that should rarely be needed.
     *
     * @return The first available Player object
     */
    public boolean isCrystalCollected(){
        return getWinnerWithCrystal() != null;
    }
    /**
     * Checks if the game has ended.
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return isGameOver;
    }

    /**
     * Gets the player whose turn it currently is.
     *
     * @return The current Player object
     */
    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

    public int getPlayerCol(){
        return lastMoveCol;
    }

    public int getPlayerRow(){
        return lastMoveRow;
    }

    public String getName(){
        return lastPlayerName;
    }

    public int getShield(){
        return lastShieldDuration;
    }


}
