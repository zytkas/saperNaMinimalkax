/**
 * @author Danylo Zhdanov 68514 and Gilhereme Santos 65443
 * Represents the game board as a 2D grid with cells containing various game elements.
 * Handles grid operations, cell management, and distance calculations. Uses 0-based internal
 * indexing but accepts 1-based Position objects for external interactions.
 */

public class Grid {
    private final char[][] gridLayout;
    private final int rows;
    private final int cols;
    private Position crystalPos;

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.gridLayout = new char[rows][cols];
    }


    /**
     * Loads a row of cells into the grid and updates crystal position if found.
     *
     * @param row The row index (0-based) to load
     * @param gridImported Array of characters representing the row's cells
     */
    public void loadRow(int row, char[] gridImported) {
        for (int i = 0; i < cols; i++) {
            gridLayout[row][i] = gridImported[i];
            if (gridImported[i] == Game.CRYSTAL_CELL) {
                crystalPos = new Position(row + 1, i + 1);
            }
        }
    }

    /**
     * Checks if a position is within the grid boundaries.
     *
     * @param pos The position to check (1-based coordinates)
     * @return true if the position is valid
     */
    public boolean isValidPosition(Position pos) {
        return pos.isValidPosition(rows, cols);
    }

    /**
     * Gets the character at the specified position in the grid.
     *
     * @param pos The position to check (1-based coordinates)
     * @return The character representing the cell's content
     */
    public char getCell(Position pos) {
        return gridLayout[pos.getRow() - 1][pos.getColumn() - 1];
    }

    /**
     * Clears a cell by setting it to an empty state ('.').
     *
     * @param pos The position to clear (1-based coordinates)
     */
    public void clearCell(Position pos) {
        gridLayout[pos.getRow() - 1][pos.getColumn() - 1] = Game.EMPTY_CELL;
    }

    /**
     * Checks if a cell is empty (contains '.').
     *
     * @param pos The position to check (1-based coordinates)
     * @return true if the cell is empty
     */
    public boolean isEmpty(Position pos) {
        return getCell(pos) == Game.EMPTY_CELL;
    }

    /**
     * Counts mines in the eight cells surrounding a position. Handles edge cases
     * when the position is near grid boundaries.
     *
     * @param pos The center position to check around (1-based coordinates)
     * @return The number of mines in adjacent cells
     */
    public int countSurroundingMines(Position pos) {
        int count = 0;
        int row = pos.getRow() - 1;
        int col = pos.getColumn() - 1;

        for (int i = Math.max(0, row - 1); i <= Math.min(rows - 1, row + 1); i++) {
            for (int j = Math.max(0, col - 1); j <= Math.min(cols - 1, col + 1); j++) {
                if (!(i == row && j == col) && gridLayout[i][j] == Game.MINE_CELL)
                    count++;
            }
        }
        return count;
    }


    /**
     * Calculates the Manhattan distance between a position and the crystal.
     *
     * @param pos The starting position (1-based coordinates)
     * @return The Manhattan distance to the crystal
     */
    public int getDistanceToCrystal(Position pos) {
        return pos.distanceManhattan(crystalPos);
    }
}
