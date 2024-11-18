import java.util.Scanner;

public class GameIO {
    private Scanner scanner;

    public GameIO() {
        scanner = new Scanner(System.in);
    }

    public String readFileName() {
        return scanner.nextLine();
    }

    public int readNumberOfPlayers() {
        int players = scanner.nextInt();
        scanner.nextLine();
        return players;
    }

    public String readCommand() {
        return scanner.nextLine();
    }

    public void printPlayerAdded(String playerName) {
        System.out.println(playerName + " was added to the game");
    }

    public void printInvalidPlacement() {
        System.out.println("Invalid player placement");
    }

    public void printGameOver() {
        System.out.println("The game is over");
    }

    public void printMoveResult(String playerName, int row, int col) {
        System.out.println(playerName + " has moved to position (" + row + ", " + col + ")");
    }

    public void close() {
        scanner.close();
    }
}