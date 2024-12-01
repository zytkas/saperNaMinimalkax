import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


public class Game {

    public static final int MOVE_SUCCESS = 0;
    public static final int MOVE_OUT_OF_BOUNDS = 1;
    public static final int MOVE_POSITION_OCCUPIED = 2;
    public static final int MOVE_MINE_HIT = 3;
    public static final int MOVE_SHIELD_PICKUP = 4;
    public static final int MOVE_CRYSTAL_FOUND = 5;
    public static final int MOVE_SUCCESS_PROTECTED = 6;

    private Grid grid;
    private Player[] players;
    private Player[] rankedPlayers;
    private int currentPlayerIndex;
    private int allPlayers;
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
        allPlayers = 0;
    }

    public boolean addPlayer(int row, int col, String name) {
        Position pos = new Position(row, col);
        if(grid.isValidPosition(pos) && grid.isEmpty(pos) && !isPositionTaken(pos)) {
            players[activePlayers] = new Player(name, row, col);
            activePlayers++;
            allPlayers++;
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
        for (int i = 0; i < allPlayers; i++) {
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
            player.finishTurn();
            nextTurn();
            return MOVE_OUT_OF_BOUNDS;
        }

        if (isPositionTaken(newPosition)) {
            player.finishTurn();
            nextTurn();
            return MOVE_POSITION_OCCUPIED;
        }

        char cell = grid.getCell(newPosition);

        // Проверяем позицию до того как закончить ход
        int result = processCell(player, newPosition, cell);

        // Только потом заканчиваем ход
        player.finishTurn();
        if (!isGameOver) {
            nextTurn();
        }

        return result;
    }

    private int processCell(Player player, Position newPosition, char cell) {
        if (cell == 'M') {
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
        } else if (cell >= '1' && cell <= '9') {
            int shieldDuration = cell - '0';
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

        if (activePlayers == 1) {
            isGameOver = true;
        }
    }

    public int detect() {
        int mines = grid.countSurroundingMines(getCurrentPlayer().getPosition());
        getCurrentPlayer().finishTurn();
        nextTurn();
        return mines;
    }

    public void skip() {
        getCurrentPlayer().finishTurn();
        nextTurn();
    }


    public MyIterator getRankedPlayers() {
        rankedPlayers = new Player[allPlayers];
        for(int i = 0; i < allPlayers; i++) {
            rankedPlayers[i] = players[i];
        }

        // Сортировка пузырьком
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

    private boolean shouldSwapPlayers(Player p1, Player p2) {
        // Сначала проверяем наличие кристалла
        if (p1.hasCollectedCrystal() != p2.hasCollectedCrystal()) {
            return !p1.hasCollectedCrystal(); // Игрок с кристаллом должен быть впереди
        }

        // Проверяем статус eliminated
        if (p1.isEliminated() != p2.isEliminated()) {
            return p1.isEliminated(); // Выбывшие игроки должны быть в конце
        }

        // Если оба eliminated, сравниваем по количеству сделанных ходов
        if (p1.isEliminated()) {
            if (p1.getTotalMoves() != p2.getTotalMoves()) {
                return p1.getTotalMoves() < p2.getTotalMoves(); // Больше ходов = выше в списке
            }
            // При равном количестве ходов - по алфавиту
            return p1.getName().compareTo(p2.getName()) > 0;
        }

        // Если оба активны, сравниваем по дистанции до кристалла
        int dist1 = grid.getDistanceToCrystal(p1.getPosition());
        int dist2 = grid.getDistanceToCrystal(p2.getPosition());
        if (dist1 != dist2) {
            return dist1 > dist2; // Меньшая дистанция = выше в списке
        }

        // При равной дистанции - по алфавиту
        return p1.getName().compareTo(p2.getName()) > 0;
    }


    private void nextTurn() {
        if(isGameOver) return;
        int startingIndex = currentPlayerIndex;
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % allPlayers;
            if (currentPlayerIndex == startingIndex) {
                isGameOver = true;
                return;
            }
        } while (players[currentPlayerIndex].isEliminated());

    }

    public Player getWinner() {
        if(isGameOver){
            // First check if anyone has collected the crystal
            for (int i = 0; i < allPlayers; i++) {
                Player player = players[i];
                if (player != null && player.hasCollectedCrystal()) {
                    return player;
                }
            }
            // If no one has the crystal, return the last active player
            for (int i = 0; i < allPlayers; i++) {
                Player player = players[i];
                if (player != null && !player.isEliminated()) {
                    return player;
                }
            }
        }
        // If somehow we get here, return the first player (should never happen in normal gameplay)
        for (int i = 0; i < allPlayers; i++) {
            if (players[i] != null) {
                return players[i];
            }
        }
        return players[0]; // Fallback, should never reach this point
    }
    public boolean isGameOver() {
        return isGameOver;
    }

    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

}
