public class Grid implements GridInterface {

    private final int rows, cols;

    public Grid(int width, int height) {
        this.rows = width;
        this.cols = height;
    }


    @Override
    public void loadRow(int x, String y) {

    }

    @Override
    public boolean isValidPosition(int x, int y) {
        return true;
    }

    @Override
    public boolean isEmpty(int x, int y) {
        return true;
    }

}
