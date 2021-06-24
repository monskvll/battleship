package academy.mindswap.ships;

public class Ship {
    private int row;
    private int col;
    private int length;
    private char direction;
    private ShipType shipType;

    public Ship(int length) {
        length = length;
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
