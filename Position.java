public class Position {
    private final int row;
    private final int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }
    public int getColumn() {
        return column;
    }

    public int distanceManhattan(Position other) {
        return Math.abs(this.row - other.row) + Math.abs(this.column - other.column);
    }

    public boolean isVaildPosition(int rowMax, int columnMax) {
        return row >= 1 && row <= rowMax && column >= 1 && column <= columnMax;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position other = (Position) obj;
        return row == other.row && column == other.column;
    }

    public Position calculateNewPosition(String direction) {
        switch (direction) {
            case "up":
                return new Position(row - 1, column);
            case "down":
                return new Position(row + 1, column);
            case "left":
                return new Position(row, column - 1);
            case "right":
                return new Position(row, column + 1);
            default:
                return this;
        }
    }
}
