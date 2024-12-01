public class MyIterator {
    int counter;
    int size;
    Player[] players;


    public MyIterator(Player[] players) {
        this.players = players;
        this.size = players.length;
        this.counter = 0;
    }

    public Player next(){
        return players[counter++];
    }

    public boolean hasNext(){
        return counter < size;
    }
}