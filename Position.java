/**
 * @author Danylo Zhdanov 68514 and Gilhereme Santos 65443
 * Represents a 2D position with row and column coordinates. Provides methods for position
 * manipulation and comparison in a grid-based system.
 */

public class Position {
    private final int row;
    private final int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Gets the row coordinate of this position.
     *
     * @return The row number (1-based indexing)
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column coordinate of this position.
     *
     * @return The column number (1-based indexing)
     */
    public int getColumn() {
        return column;
    }

    /**
     * Calculates the Manhattan distance between this position and another.
     *
     * @param other The position to calculate distance to
     * @return The Manhattan distance (|x1-x2| + |y1-y2|)
     */
    public int distanceManhattan(Position other) {
        return Math.abs(this.row - other.row) + Math.abs(this.column - other.column);
    }

    /**
     * Checks if this position is within the specified bounds.
     *
     * @param rowMax Maximum valid row number
     * @param columnMax Maximum valid column number
     * @return true if position is within bounds (1 to max inclusive)
     */
    public boolean isValidPosition(int rowMax, int columnMax) {
        return row >= 1 && row <= rowMax && column >= 1 && column <= columnMax;
    }

    /**
     * Checks if this position equals another object.
     *
     * @param obj The object to compare with
     * @return true if obj is a Position with same row and column
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position other)) return false;
        return row == other.row && column == other.column;
    }

    /**
     * Creates a new Position moved one step in the specified direction.
     *
     * @param direction The direction to move ("up", "down", "left", "right")
     * @return A new Position object representing the new location
     */
    public Position calculateNewPosition(String direction) {
        return switch (direction) {
            case "up" -> new Position(row - 1, column);
            case "down" -> new Position(row + 1, column);
            case "left" -> new Position(row, column - 1);
            case "right" -> new Position(row, column + 1);
            default -> this;
        };
    }
}
