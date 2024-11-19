import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Game {
    private GameIO gameIO;
    private Grid grid;
    private Player[] players;
    private int currentPlayerIndex;
    private int activePlayerIndex;
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
        } catch (FileNotFoundException e) {

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
            players[activePlayerIndex] = new Player(name, row, col);
            activePlayerIndex++;
            gameIO.printPlayerAdded(name);
        }else{
            gameIO.printInvalidPlacement();
        }

    }

    private boolean IsPositionTaken(int row, int col){
        return true;
    }

    private void processCommand(String command) {
    }

    private void processQuit() {
    }
}