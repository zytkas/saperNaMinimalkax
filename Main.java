import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class Main {
    private static final String PLAYER_ADDED = "%s was added to the game%n";
    private static final String INVALID_PLACEMENT = "Invalid player placement%n";
    private static final String INVALID_COMMAND = "Invalid command%n";
    private static final String GAME_OVER = "The game is over%n";
    private static final String GAME_NOT_OVER = "The game was not over yet%n";
    private static final String WINNER_CRYSTAL = "%s has won as they collected the cosmic crystal%n";
    private static final String WINNER_LAST = "%s has won as they are the only active player%n";
    private static final String OUT_OF_BOUNDS = "%s cannot move outside the grid%n";
    private static final String POSITION_TAKEN = "The position is occupied%n";
    private static final String MOVE_RESULT = "%s has moved to position (%d, %d)%n";
    private static final String SHIELD_PICKUP = "%s picked up a shield and is now protected for %d turns%n";
    private static final String PROTECTED = "%s is protected for %d more turns%n";
    private static final String MINE_PROTECTION = "%s is protected from the proton mine%n";
    private static final String STEPPED_MINE = "%s stepped on a mine%n";
    private static final String CRYSTAL_FOUND = "%s found the cosmic crystal%n";
    private static final String MINES_AROUND = "There are %d mines around the cell%n";
    private static final String SKIP_TURN = "%s skipped their turn%n";
    private static final String RANK_FORMAT = "%s:(%d, %d) %d %s%n";

    private static Game game;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String filename = scanner.nextLine();
        game = new Game(filename);

        int numPlayers = scanner.nextInt();
        scanner.nextLine();
        game.initializePlayers(numPlayers);

        for (int i = 0; i < numPlayers; i++) {
            String playerInput = scanner.nextLine();
            handlePlayerAdd(playerInput);
        }

        String command;
        while (!(command = scanner.nextLine()).equals("quit")) {
            processCommand(command);
        }
        handleQuit();

        scanner.close();
    }



    private static void handlePlayerAdd(String input) {
        String[] parts = input.split(" ", 4);
        if (parts.length < 4 || !parts[0].equals("player")) {
            System.out.format(INVALID_COMMAND);
            return;
        }

        int row = parseInt(parts[1]);
        int col = parseInt(parts[2]);
        String name = parts[3];

        if (game.addPlayer(row, col, name)) {
            System.out.format(PLAYER_ADDED, name);
        } else {
            System.out.format(INVALID_PLACEMENT);
        }
    }

    private static void processCommand(String command) {
        String[] parts = command.split(" ");
        Player currentPlayer = game.getCurrentPlayer();

        if (game.isGameOver() && !parts[0].equals("rank") && !parts[0].equals("quit")) {
            System.out.format(GAME_OVER);
            return;
        }

        switch (parts[0]) {
            case "move":
                handleMove(currentPlayer, parts[1]);
                break;
            case "detect":
                handleDetect();
                break;
            case "skip":
                System.out.format(SKIP_TURN, currentPlayer.getName());
                handleSkip();
                break;
            case "rank":
                handleRank();
                return;
            default:
                System.out.format(INVALID_COMMAND);
        }
    }

    private static void handleMove(Player player, String direction) {
        int result = game.movePlayer(direction);
        switch (result) {
            case Game.MOVE_SUCCESS:
                Position pos = player.getPosition();
                System.out.format(MOVE_RESULT, player.getName(), pos.getRow(), pos.getColumn());
                break;
            case Game.MOVE_OUT_OF_BOUNDS:
                System.out.format(OUT_OF_BOUNDS, player.getName());
                break;
            case Game.MOVE_POSITION_OCCUPIED:
                System.out.format(POSITION_TAKEN);
                break;
            case Game.MOVE_MINE_HIT:
                System.out.format(STEPPED_MINE, player.getName());
                break;
            case Game.MOVE_SHIELD_PICKUP:
                System.out.format(SHIELD_PICKUP, player.getName(), player.getShieldDuration());
                break;
            case Game.MOVE_CRYSTAL_FOUND:
                System.out.format(CRYSTAL_FOUND, player.getName());
                break;
        }
    }

    private static void handleDetect() {
        int mines = game.detect();
        if (mines >= 0) {
            System.out.format(MINES_AROUND, mines);
        }
    }

    private static void handleSkip() {
        game.skip();
        System.out.format(SKIP_TURN, game.getCurrentPlayer().getName());
    }


    private static void handleRank() {
        Player[] ranked = game.getRankedPlayers();
        for (Player p : ranked) {
            System.out.format(RANK_FORMAT,
                    p.getName(),
                    p.getPosition().getRow(),
                    p.getPosition().getColumn(),
                    p.getShieldDuration(),
                    p.isEliminated() ? "eliminated" : "active"
            );
        }
    }

    private static void handleQuit() {
        if (!game.isGameOver()) {
            System.out.format(GAME_NOT_OVER);
            return;
        }

        Player winner = game.getWinner();
        if (winner.hasCollectedCrystal()) {
            System.out.format(WINNER_CRYSTAL, winner.getName());
        } else {
            System.out.format(WINNER_LAST, winner.getName());
        }
    }
}