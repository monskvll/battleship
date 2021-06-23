package academy.mindswap;

public class Ship {
    private int row;
    private int col;
    private int length;
    private char direction;

    public Ship(int length) {
        this.length = length;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setDirection(char direction) {
        if (direction == 'H') {

        } else if (direction == 'V') {

        } else {
            System.out.println("Choose a valid direction.");
            setDirection(direction);
        }
    }
}
