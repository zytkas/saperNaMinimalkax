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

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    public int getTotalMoves(){
        return totalMoves;
    }

    public void incrementTotalMoves(){
        totalMoves++;
    }


    public void moveTo(Position newPosition) {
        this.position = newPosition;
    }

        public void addShield(int duration) {
            // Если есть активный щит, добавляем к нему
            if (shieldDuration > 0) {
                shieldDuration += duration;
            }
            // Если нет активного щита, устанавливаем pendingShield
            else {
                pendingShieldDuration = duration;
            }
        }

        public boolean isProtected() {
            return shieldDuration > 0;
        }

        public void finishTurn() {
            incrementTotalMoves();

            // Если есть активный щит, уменьшаем его
            if (shieldDuration > 0) {
                shieldDuration--;
            }

            // Если есть отложенный щит, активируем его
            if (pendingShieldDuration > 0) {
                shieldDuration = pendingShieldDuration;
                pendingShieldDuration = 0;
            }
        }

        public int getShieldDuration() {
            // Для вывода показываем большее из значений
            return Math.max(shieldDuration, pendingShieldDuration);
        }
    public boolean isEliminated() {
        return isEliminated;
    }

    public void eliminate() {
        this.isEliminated = true;
        this.shieldDuration = 0;
    }
    public boolean hasCollectedCrystal() {
        return hasCollectedCrystal;
    }

    public void collectCrystal() {
        this.hasCollectedCrystal = true;
    }
}