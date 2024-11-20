import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Game {
    private final GameIO gameIO;
    private Grid grid;
    private Player[] players;
    private int currentPlayerIndex;
    private int activePlayers;
    private boolean isGameOver;

    public Game() {
        gameIO = new GameIO();
        isGameOver = false;
        currentPlayerIndex = 0;
    }

    public void start() {
        initializeGame();
        addPlayers();
        startGameLoop();
        gameIO.close();
    }

    private void initializeGame(){
        String filename = gameIO.readFileName();
        loadGrid(filename);
    }

    private void addPlayers() {
        int numPlayers = gameIO.readNumberOfPlayers();
        players = new Player[numPlayers];

        for (int i = 0; i < numPlayers; i++) {
            processPlayerInput(gameIO.readCommand());
        }
    }

    private void startGameLoop() {
        String command;
        while (!(command = gameIO.readCommand()).equals("quit")) {
            if (isGameOver){
                gameIO.printGameOver();
                continue;
            }
            processCommand(command);
        }
        processQuit();
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

    private void processPlayerInput(String input) {
        String[] parts = input.split(" ", 4);
        if (parts.length < 4 || !parts[0].equals("player")) {
            return;
        }
        int row = gameIO.parseNumber(parts[1]);
        int col = gameIO.parseNumber(parts[2]);
        String name = parts[3];

        Position checkPosition = new Position(row, col);

        if(grid.isValidPosition(checkPosition) && grid.isEmpty(checkPosition) && !isPositionTaken(checkPosition)) {
            players[activePlayers] = new Player(name, row, col);
            activePlayers++;
            gameIO.printPlayerAdded(name);
        }else{
            gameIO.printInvalidPlacement();
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
    private void processCommand(String command) {
            String[] parts = command.split(" ");
            Player player = players[currentPlayerIndex];

            switch (parts[0]) {
                case "move":
                    processMoveCommand(player, parts[1]);
                    break;
                case "detect":
                    processDetectCommand(player);
                    break;
                case "skip":
                    processSkipCommand(player);
                    break;
                case "rank":
                    processRankCommand();
                    return;
                default:
                    gameIO.printInvalidCommand();
                    return;
            }
            nextTurn();
    }

    private void processMoveCommand(Player player, String direction) {
        Position newPosition = player.getPosition().calculateNewPosition(direction);

        if(!grid.isValidPosition(newPosition)) {
            gameIO.printOutOfBounds(player.getName());
            return;
        }

        if(isPositionTaken(newPosition)) {
            gameIO.printPositionTaken();
            return;
        }

        char cell = grid.getCell(newPosition);

        if (cell == 'M') {
            if (player.isProtected()) {
                //print protected from mine
                grid.clearCell(newPosition);
            }else{
                //print stepped on mine
                eliminatePlayer(player);
                return;
            }
        } else if (cell >= '1' && cell <= '9'){
            int shiledDuration = Character.getNumericValue(cell);
            player.addShield(shiledDuration);
            //print shield pick up
            grid.clearCell(newPosition);
        } else if (cell == 'X'){
            isGameOver = true;
            player.collectCrystal();
            //print player has stepped on crystal
            return;
        }

        player.moveTo(newPosition);
        //print player moved
    }

    private void eliminatePlayer(Player player) {
        player.eliminate();
        activePlayers--;

        if (getActivePlayersCount() == 1) {
            isGameOver = true;
        }
    }

    private int getActivePlayersCount() {
        int count = 0;
        for (int i = 0; i < players.length; i++) {
            if (!players[i].isEliminated()) {
                count++;
            }
        }
        return count;
    }

    private void processDetectCommand(Player player) {
        int mines = grid.countSurroundingMines(player.getPosition());
        //print number of mines
    }

    private void processSkipCommand(Player player) {
        //print move skipped
    }

    private void processRankCommand() {
        Player[] rankedPlayers = getRankedPlayers();
        for(Player player : rankedPlayers) {
            //print player status
        }
    }

    private Player[] getRankedPlayers() {
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
        if (players[currentPlayerIndex].isProtected()) {
            players[currentPlayerIndex].decreaseShield();
        }

        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
        }while (players[currentPlayerIndex].isEliminated() && !isGameOver);
    }

    private Player getWinner() {
        if(isGameOver){
            for (Player player : players) {
                if (!player.isEliminated()){
                    return player;
                }
            }
        }
        return players[0];
    }
    private void processQuit() {
        if(!isGameOver) {
            gameIO.printGameNotOverYet();
            return;
        }
        Player winner = getWinner();

        if(winner.hasCollectedCrystal()){
            gameIO.printWinnerWithCrystal(winner.getName());
        }else   {
            gameIO.printWinnerLastActive(winner.getName());
        }
     }
    }
