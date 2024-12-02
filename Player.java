/**
 * @author Danylo Zhdanov 68514 and Gilhereme Santos 65443
 * Represents a player in the game with their state, position, and abilities.
 * Manages player movement, shield protection, crystal collection, and elimination status.
 */

public class Player {
    private final String name;
    private Position position;
    private boolean isEliminated;
    private int shieldDuration;
    private boolean hasCollectedCrystal;
    private int totalMoves;
    private int pendingShieldDuration;

    public Player(String name, int row, int col) {
        this.name = name;
        this.position = new Position(row, col);
        this.isEliminated = false;
        this.shieldDuration = 0;
        this.totalMoves = 0;
        this.hasCollectedCrystal = false;
    }

    /**
     * Gets the player's name.
     *
     * @return The player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player's current position.
     *
     * @return The current Position object
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gets the total number of moves made by this player.
     *
     * @return Total moves count
     */
    public int getTotalMoves() {
        return totalMoves;
    }

    /**
     * Increments the player's move counter.
     */
    public void incrementTotalMoves() {
        totalMoves++;
    }

    /**
     * Updates the player's position to a new location.
     *
     * @param newPosition The new position to move to
     */
    public void moveTo(Position newPosition) {
        this.position = newPosition;
    }

    /**
     * Adds shield protection to the player. If a shield is already active,
     * extends its duration. Otherwise, sets up a pending shield that activates
     * at the end of the current turn.
     *
     * @param duration The number of turns the shield will last
     */
    public void addShield(int duration) {
        if (shieldDuration > 0) {
            shieldDuration += duration;
        }
        else {
            pendingShieldDuration = duration;
        }
    }

    /**
     * Checks if the player is currently protected by a shield.
     *
     * @return true if the player has an active shield
     */
    public boolean isProtected() {
        return shieldDuration > 0;
    }

    /**
     * Processes end-of-turn actions:
     * - Increments move counter
     * - Decrements active shield duration
     * - Activates any pending shield
     */
    public void finishTurn() {
        incrementTotalMoves();

        if (shieldDuration > 0) {
            shieldDuration--;
        }

        if (pendingShieldDuration > 0) {
            shieldDuration = pendingShieldDuration;
            pendingShieldDuration = 0;
        }
    }

    /**
     * Gets the total remaining shield duration, including both active and pending shields.
     *
     * @return The maximum of current shield duration and pending shield duration
     */
    public int getShieldDuration() {
        return Math.max(shieldDuration, pendingShieldDuration);
    }

    /**
     * Checks if the player has been eliminated from the game.
     *
     * @return true if the player is eliminated
     */
    public boolean isEliminated() {
        return isEliminated;
    }

    /**
     * Eliminates the player from the game and removes any active shields.
     */
    public void eliminate() {
        this.isEliminated = true;
        this.shieldDuration = 0;
    }

    /**
     * Checks if the player has collected the crystal.
     *
     * @return true if the player has the crystal
     */
    public boolean hasCollectedCrystal() {
        return hasCollectedCrystal;
    }

    /**
     * Marks that this player has collected the crystal.
     */
    public void collectCrystal() {
        this.hasCollectedCrystal = true;
    }
}