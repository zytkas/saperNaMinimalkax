public interface PlayerInterface {
    boolean hasCollectedCrystal();
    boolean isEliminated();
    String getName();
    boolean isProtected();
    void decreaseShield();
}
