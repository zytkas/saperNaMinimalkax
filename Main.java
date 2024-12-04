/**
 * @author Danylo Zhdanov 68514 and Gilhereme Santos 65443
 * Main controller class for a grid-based strategy game where players navigate through
 * a field of mines to find a crystal. This class handles:
 *
 * - Game initialization (grid loading and player setup)
 * - Command processing and game flow control
 * - User input/output and message formatting
 * - Game state management and win condition verification
 *
 * The game loop processes commands like move, detect, skip, and rank until a quit command
 * is received or the game ends through crystal collection or player elimination.
 */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import static java.lang.Integer.parseInt;

public class Main {
    /**
     * Command constants used for game actions.
     * These strings define the exact command words that the game recognizes
     * and are used for command validation and processing.
     */
    private static final String CMD_MOVE = "move";
    private static final String CMD_DETECT = "detect";
    private static final String CMD_SKIP = "skip";
    private static final String CMD_RANK = "rank";
    private static final String CMD_QUIT = "quit";

    /**
     * Game state messages used to communicate game status and validity.
     * These messages handle core game flow events like game ending,
     * invalid actions, and system-level responses.
     */
    private static final String MSG_INVALID_COMMAND = "Invalid command%n";
    private static final String MSG_INVALID_PLACEMENT = "Invalid player placement%n";
    private static final String MSG_GAME_OVER = "The game is over%n";
    private static final String MSG_GAME_NOT_OVER = "The game was not over yet%n";
    private static final String MSG_WINNER_CRYSTAL =
            "%s has won as they collected the cosmic crystal%n";
    private static final String MSG_WINNER_LAST = "%s has won as they are the only active player%n";

    /**
     * Action feedback messages used to communicate results of player actions.
     * These messages provide immediate feedback for all player interactions
     * including movement, item pickups, and status updates.
     */
    private static final String MSG_PLAYER_ADDED = "%s was added to the game%n";
    private static final String MSG_OUT_OF_BOUNDS = "%s cannot move outside the grid%n";
    private static final String MSG_POSITION_TAKEN = "The position is occupied%n";
    private static final String MSG_MOVE_RESULT = "%s has moved to position (%d, %d)%n";
    private static final String MSG_SHIELD_PICKUP = "%s is protected for %d turns%n";
    private static final String MSG_PROTECTED = "%s is protected by a force shield%n";
    private static final String MSG_STEPPED_MINE = "%s stepped into a proton mine%n";
    private static final String MSG_CRYSTAL_FOUND = "%s has won%n";
    private static final String MSG_MINES_AROUND = "There are %d mines around the cell%n";
    private static final String MSG_SKIP_TURN = "%s skipped their turn%n";
    private static final String MSG_RANK_FORMAT = "%s: (%d, %d) %d %s%n";
    private static final String MSG_PLAYER_ELIMINATED = "eliminated";
    private static final String MSG_PLAYER_ACTIVE = "active";

    /**
     * The main entry point for the game application.
     * Initializes the game grid, sets up players, and processes game commands until quit.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String filename = in.nextLine();
        Grid grid = loadGridFromFile(filename);
        Game game = new Game(grid);

        initializePlayers(in, game);
        processGameCommands(in, game);

        in.close();
    }

    /**
     * Loads and initializes the game grid from a specified file.
     * The file format should contain:
     * - First line: two integers representing rows and columns
     * - Subsequent lines: grid layout with characters representing different game elements
     *
     * @param filename The path to the grid configuration file
     * @return A new Grid object initialized with the file contents
     * @throws IllegalArgumentException if the file is not found or has invalid format
     */

    private static Grid loadGridFromFile(String filename) {
        try {
            Scanner file = new Scanner(new FileReader(filename));
            int rows = file.nextInt();
            int cols = file.nextInt();
            file.nextLine();

            Grid grid = new Grid(rows, cols);

            for (int i = 0; i < rows; i++) {
                char[] row = file.nextLine().toCharArray();
                grid.loadRow(i, row);
            }
            file.close();
            return grid;
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(filename);
        }
    }

    /**
     * Initializes game players based on user input.
     * Reads the number of players and their initial positions from the input scanner.
     *
     * @param in Scanner object for reading player information
     * @param game The game instance to initialize players in
     */
    private static void initializePlayers(Scanner in, Game game) {
        int numPlayers = in.nextInt();
        in.nextLine();
        game.initializePlayers(numPlayers);

        for (int i = 0; i < numPlayers; i++) {
            String playerInput = in.nextLine();
            addPlayer(playerInput, game);
        }
    }

    /**
     * Adds a new player to the game at specified position.
     * Format of in string: "row col name"
     *
     * @param in String containing player information
     * @param game The game instance to add the player to
     */
    private static void addPlayer(String in, Game game) {
        String[] parts = in.split(" ", 4);

        int row = parseInt(parts[1]);
        int col = parseInt(parts[2]);
        String name = parts[3];

        if (game.addPlayer(row, col, name)) {
            System.out.printf(MSG_PLAYER_ADDED, name);
        } else {
            System.out.printf(MSG_INVALID_PLACEMENT);
        }
    }

    /**
     * Processes game commands until the "quit" command is received.
     * Handles movement, detection, skipping turns, and ranking commands.
     *
     * @param in Scanner object for reading commands
     * @param game The game instance to process commands for
     */
    private static void processGameCommands(Scanner in, Game game) {
        String command;
        while (!(command = in.nextLine()).equals(CMD_QUIT)) {
            processCommand(command, game);
        }
        handleQuit(game);
    }

    /**
     * Processes a single game command and executes corresponding action.
     * Validates the command and checks if the game is still active before execution.
     *
     * @param command The command string to process
     * @param game The game instance to execute the command on
     */
    private static void processCommand(String command, Game game) {
        String[] parts = command.split(" ");
        String action = parts[0];

        if (!isValidCommand(action)) {
            System.out.printf(MSG_INVALID_COMMAND);
            return;
        }

        if (game.isGameOver() && !action.equals(CMD_RANK)) {
            System.out.printf(MSG_GAME_OVER);
            return;
        }

        executeCommand(action, parts, game);
    }

    /**
     * Validates if the given command is one of the allowed game commands.
     * Valid commands are: move, detect, skip, and rank.
     *
     * @param command The command string to validate
     * @return true if the command is valid, false otherwise
     */
    private static boolean isValidCommand(String command) {
        return command.matches(CMD_MOVE + "|" + CMD_DETECT + "|" + CMD_SKIP + "|" + CMD_RANK);
    }

    /**
     * Executes the specified game command with given parameters.
     * Routes the command to appropriate handler method based on action type.
     *
     * @param action The type of action to execute (move, detect, skip, rank)
     * @param parts Array of command parts including parameters
     * @param game The game instance to execute the command on
     */
    private static void executeCommand(String action, String[] parts, Game game) {
        switch (action) {
            case CMD_MOVE -> handleMove(parts[1], game);
            case CMD_DETECT -> handleDetect(game);
            case CMD_SKIP -> handleSkip(game);
            case CMD_RANK -> handleRank(game);
            default -> throw new IllegalStateException();
        }
    }

    /**
     * Handles player movement commands.
     * Processes movement results and displays appropriate messages for:
     * - Successful moves
     * - Protected moves
     * - Out of bounds movements
     * - Occupied positions
     * - Mine hits
     * - Shield pickups
     * - Crystal finding
     * @param direction The direction to move the player
     * @param game The game instance to execute the movement in
     */
    private static void handleMove(String direction, Game game) {
        Player player = game.getCurrentPlayer();
        int result = game.movePlayer(direction);
        Position pos = player.getPosition();
        switch (result) {
            case Game.MOVE_SUCCESS -> System.out.printf(MSG_MOVE_RESULT, player.getName(),
                    pos.getRow(), pos.getColumn());
            case Game.MOVE_SUCCESS_PROTECTED -> System.out.printf(MSG_PROTECTED, player.getName());
            case Game.MOVE_OUT_OF_BOUNDS -> System.out.printf(MSG_OUT_OF_BOUNDS, player.getName());
            case Game.MOVE_POSITION_OCCUPIED -> System.out.printf(MSG_POSITION_TAKEN);
            case Game.MOVE_MINE_HIT -> System.out.printf(MSG_STEPPED_MINE, player.getName());
            case Game.MOVE_SHIELD_PICKUP -> System.out.printf(MSG_SHIELD_PICKUP, player.getName(),
                    player.getShieldDuration());
            case Game.MOVE_CRYSTAL_FOUND -> System.out.printf(MSG_CRYSTAL_FOUND, player.getName());
            default -> throw new IllegalStateException();
        }
    }

    /**
     * Handles mine detection command.
     * Displays the number of mines in adjacent positions to the current player.
     *
     * @param game The game instance to perform detection in
     */
    private static void handleDetect(Game game) {
        int mines = game.detect();
        System.out.printf(MSG_MINES_AROUND, mines);

    }

    /**
     * Handles skip turn command.
     * Allows current player to skip their turn and moves to next player.
     *
     * @param game The game instance to skip turn in
     */
    private static void handleSkip(Game game) {
        System.out.printf(MSG_SKIP_TURN, game.getCurrentPlayer().getName());
        game.skip();
    }

    /**
     * Handles rank display command.
     * Displays current ranking of all players including their:
     * - Position
     * - Shield duration
     * - Elimination status
     * @param game The game instance to display rankings for
     */
    private static void handleRank(Game game) {
        MyIterator iterator = game.getRankedPlayers();

        while (iterator.hasNext()) {
            Player p = iterator.next();
            System.out.printf(MSG_RANK_FORMAT,
                    p.getName(),
                    p.getPosition().getRow(),
                    p.getPosition().getColumn(),
                    p.getShieldDuration(),
                    p.isEliminated() ? MSG_PLAYER_ELIMINATED : MSG_PLAYER_ACTIVE
            );
        }
    }

    /**
     * Handles game quit command.
     * Verifies if game is over and announces winner based on:
     * - Crystal collection
     * - Last player standing
     *
     * @param game The game instance to handle quit for
     */
    private static void handleQuit(Game game) {
        if (!game.isGameOver()) {
            System.out.printf(MSG_GAME_NOT_OVER);
            return;
        }

        Player winner = game.getWinner();
        if (winner.hasCollectedCrystal()) {
            System.out.printf(MSG_WINNER_CRYSTAL, winner.getName());
        } else {
            System.out.printf(MSG_WINNER_LAST, winner.getName());
        }
    }
}