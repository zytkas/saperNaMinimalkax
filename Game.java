import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Game {
    private final GameIO gameIO;
    private Grid grid;
    private Player[] players;
    private int currentPlayerIndex;
    private int activePlayers;
    private final boolean isGameOver;

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
                String row = file.nextLine();
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

        if(grid.isValidPosition(row,col) && grid.isEmpty(row, col) && !IsPositionTaken(row, col)){
            players[activePlayers] = new Player(name, row, col);
            activePlayers++;
            gameIO.printPlayerAdded(name);
        }else{
            gameIO.printInvalidPlacement();
        }

    }


    //need to do
    private boolean IsPositionTaken(int row, int col){
        return false;
    }

    private void processCommand(String command) {
            String[] parts = command.split(" ");
            Player player = players[currentPlayerIndex];

            switch (parts[0]) {
                case "move":
                    //do move command
                    break;
                case "detect":
                    //do detect command
                    break;
                case "skip":
                    //do skip command
                    break;
                case "rank":
                    //do rank command
                    return;
                default:
                    gameIO.printInvalidCommand();
                    return;
            }
            nextTurn();
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
            gameIO.printGameOver();
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
