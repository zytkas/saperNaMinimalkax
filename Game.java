import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


public class Game {

    // Константы для результатов движения
    public static final int MOVE_SUCCESS = 0;
    public static final int MOVE_OUT_OF_BOUNDS = 1;
    public static final int MOVE_POSITION_OCCUPIED = 2;
    public static final int MOVE_MINE_HIT = 3;
    public static final int MOVE_SHIELD_PICKUP = 4;
    public static final int MOVE_CRYSTAL_FOUND = 5;

    private Grid grid;
    private Player[] players;
    private int currentPlayerIndex;
    private int activePlayers;
    private boolean isGameOver;

    public Game(String filename) {
        isGameOver = false;
        currentPlayerIndex = 0;
        loadGrid(filename);
    }


    public void initializePlayers(int numPlayers) {
        players = new Player[numPlayers];
        activePlayers = 0;
    }

    public boolean addPlayer(int row, int col, String name) {
        Position pos = new Position(row, col);
        if(grid.isValidPosition(pos) && grid.isEmpty(pos) && !isPositionTaken(pos)) {
            players[activePlayers] = new Player(name, row, col);
            activePlayers++;
            return true;
        }
        return false;
    }


    private void loadGrid(String filename){
        try {
            Scanner file = new Scanner(new FileReader(filename));
            int rows = file.nextInt();
            int cols = file.nextInt();
            file.nextLine();

            grid = new Grid(rows, cols);

            for (int i = 0; i < rows; i++) {
                char[] row = file.nextLine().toCharArray();
                grid.loadRow(i, row);
            }
            file.close();
        } catch (FileNotFoundException ignored) {

        }
    }


    private boolean isPositionTaken(Position pos) {
        for (int i = 0; i < activePlayers; i++) {
            if (!players[i].isEliminated() &&
                    players[i].getPosition().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    public int movePlayer(String direction) {
        Player player = getCurrentPlayer();
        Position newPosition = player.getPosition().calculateNewPosition(direction);

        if (!grid.isValidPosition(newPosition)) {
            return MOVE_OUT_OF_BOUNDS;
        }

        if (isPositionTaken(newPosition)) {
            return MOVE_POSITION_OCCUPIED;
        }

        char cell = grid.getCell(newPosition);
        int result = processCell(player, newPosition, cell);

        if (result != MOVE_OUT_OF_BOUNDS && result != MOVE_POSITION_OCCUPIED) {
            nextTurn();
        }

        return result;
    }

    private int processCell(Player player, Position newPosition, char cell) {
        if (cell == 'M') {
            if (player.isProtected()) {
                grid.clearCell(newPosition);
                player.moveTo(newPosition);
                return MOVE_SUCCESS;
            } else {
                eliminatePlayer(player);
                return MOVE_MINE_HIT;
            }
        } else if (cell >= '1' && cell <= '9') {
            int shieldDuration = Character.getNumericValue(cell);
            player.addShield(shieldDuration);
            grid.clearCell(newPosition);
            player.moveTo(newPosition);
            return MOVE_SHIELD_PICKUP;
        } else if (cell == 'X') {
            isGameOver = true;
            player.collectCrystal();
            player.moveTo(newPosition);
            return MOVE_CRYSTAL_FOUND;
        }

        player.moveTo(newPosition);
        return MOVE_SUCCESS;
    }

    private void eliminatePlayer(Player player) {
        player.eliminate();
        activePlayers--;

        if (getActivePlayersCount() == 1) {
            isGameOver = true;
        }
    }

    public int detect() {
        int mines = grid.countSurroundingMines(getCurrentPlayer().getPosition());
        nextTurn();
        return mines;
    }

    public void skip() {
        nextTurn();
    }

    private int getActivePlayersCount() {
        int count = 0;
        for (int i = 0; i < activePlayers; i++) {
            if (!players[i].isEliminated()) {
                count++;
            }
        }
        return count;
    }


    public Player[] getRankedPlayers() {
        // Создаем новый массив такого же размера
        Player[] ranked = new Player[players.length];

        // Вручную копируем элементы
        for(int i = 0; i < players.length; i++) {
            ranked[i] = players[i];
        }

        // Сортировка пузырьком по нашим правилам
        for (int i = 0; i < ranked.length - 1; i++) {
            for (int j = 0; j < ranked.length - i - 1; j++) {
                if (shouldSwapPlayers(ranked[j], ranked[j + 1])) {
                    // Меняем местами
                    Player temp = ranked[j];
                    ranked[j] = ranked[j + 1];
                    ranked[j + 1] = temp;
                }
            }
        }

        return ranked;
    }

    private boolean shouldSwapPlayers(Player p1, Player p2) {
        if (p1.isEliminated() != p2.isEliminated()) {
            return p1.isEliminated(); // eliminated players go last
        }

        if (p1.isEliminated()) {
            return p1.getName().compareTo(p2.getName()) > 0;
        } else {
            int dist1 = grid.getDistanceToCrystal(p1.getPosition());
            int dist2 = grid.getDistanceToCrystal(p2.getPosition());
            if (dist1 != dist2) {
                return dist1 > dist2;
            }
            return p1.getName().compareTo(p2.getName()) > 0;
        }
    }

    private void nextTurn() {
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % activePlayers;
            if (players[currentPlayerIndex].isProtected()) {
                players[currentPlayerIndex].decreaseShield();
            }
        }while (players[currentPlayerIndex].isEliminated() && !isGameOver);
    }

    public Player getWinner() {
        if(isGameOver){
            for (Player player : players) {
                if (!player.isEliminated()){
                    return player;
                }
            }
        }
        return players[0];
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

}
