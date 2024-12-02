/**
 * @author Danylo Zhdanov 68514 and Gilhereme Santos 65443
 * A custom iterator implementation for traversing an array of Player objects.
 * Provides sequential access to players in the order they appear in the array.
 */

public class MyIterator {
    int counter;
    int size;
    Player[] players;


    public MyIterator(Player[] players) {
        this.players = players;
        this.size = players.length;
        this.counter = 0;
    }


    /**
     * Returns the next player in the iteration and advances the counter.
     *
     * @return The next Player object
     * @throws ArrayIndexOutOfBoundsException if called when hasNext() is false
     */
    public Player next(){
        return players[counter++];
    }


    /**
     * Checks if there are more players to iterate over.
     *
     * @return true if there are more players, false otherwise
     */
    public boolean hasNext(){
        return counter < size;
    }
}