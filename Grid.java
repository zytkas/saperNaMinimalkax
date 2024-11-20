public class Grid {

    private final char[][] grid;
    private final int rows, cols;
    private Position crystalPos;

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new char[rows][cols];
    }

    public void loadRow(int row, char[] gridImported) {
        for (int i = 0; i < cols; i++) {
            grid[row][i] = gridImported[i];
            if (gridImported[i] == 'X'){
                crystalPos = new Position(row + 1, i + 1);
            }
        }
    }

    public boolean isValidPosition(Position pos) {
        return pos.isVaildPosition(rows, cols);
    }

    public char getCell(Position pos) {
        return grid[pos.getRow() - 1][pos.getColumn() - 1];
    }

    public void clearCell(Position pos) {
        grid[pos.getRow() - 1][pos.getColumn() - 1] = '.';
    }
    public boolean isEmpty(Position pos) {
        return getCell(pos) == '.';
    }

    public int countSurroundingMines(Position pos) {
        int count = 0;
        int row = pos.getRow() - 1;
        int col = pos.getColumn() - 1;

        for(int i = Math.max(0, row - 1); i <= Math.min(rows - 1, row + 1); i++) {
            for(int j = Math.max(0, col - 1); j <= Math.min(cols - 1, col + 1); j++) {
                if(i == row && j == col) continue;  // skip current pos
                if(grid[i][j] == 'M') count++;
            }
        }
        return count;
    }

    public int getDistanceToCrystal(Position pos) {
        return pos.distanceManhattan(crystalPos);
    }
}
