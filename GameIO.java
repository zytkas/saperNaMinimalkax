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

    public void printInvalidCommand() {
        System.out.println("Invalid command");
    }

    public void printGameOver() {
        System.out.println("The game is over");
    }

    public void printGameNotOverYet() {
        System.out.println("The game was not over yet");
    }

    public void printWinnerWithCrystal(String name) {
        System.out.println(name + " has won as they collected the cosmic crystal");
    }

    public void printWinnerLastActive(String name) {
        System.out.println(name + " has won as they are the only active player");
    }

    public void printMoveResult(String playerName, int row, int col) {
        System.out.println(playerName + " has moved to position (" + row + ", " + col + ")");
    }

    public int parseNumber(String number) {
        int result = 0;
        int i = 0;

        while (i < number.length()) {
                result = result * 10 + (number.charAt(i) - '0'); //Moving ASCII number '0' is 48, 1 is 49, etc...
                i++;
            }
        return result;
    }

    public void close() {
        scanner.close();
    }
}