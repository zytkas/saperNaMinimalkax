public class Player {
    private final String name;
    private Position position;
    private boolean isEliminated;
    private int shieldDuration;
    private boolean hasCollectedCrystal;


    public Player(String name, int row, int col) {
        this.name = name;
        this.position = new Position(row, col);
        this.isEliminated = false;
        this.shieldDuration = 0;
        this.hasCollectedCrystal = false;
    }

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    public void moveTo(Position newPosition) {
        this.position = newPosition;
    }


    public boolean isEliminated() {
        return isEliminated;
    }

    public void eliminate() {
        this.isEliminated = true;
    }

    public boolean isProtected() {
        return shieldDuration > 0;
    }

    public void decreaseShield() {
        if (shieldDuration > 0) {
            shieldDuration--;
        }
    }

    public void addShield(int duration) {
        shieldDuration += duration;
    }

    public int getShieldDuration() {
        return shieldDuration;
    }

    public boolean hasCollectedCrystal() {
        return hasCollectedCrystal;
    }

    public void collectCrystal() {
        this.hasCollectedCrystal = true;
    }
}