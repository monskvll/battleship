package academy.mindswap;

public class Board {
    private int width;
    private int height;
    private char[][] matrix;
    private char water = '~';
    private char ship = 'H';
    private char hit = 'X';
    private char miss = 'O';
    private char shipwreck = '@';

    public Board() {
        width = 10;
        height = 10;
        matrix = new char[width][height];
    }

    public void createBoard() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = water;
            }
        }
    }

    public char[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(char[][] matrix) {
        this.matrix = matrix;
    }

    public char getWater() {
        return water;
    }

    public char getShip() {
        return ship;
    }

    public char getHit() {
        return hit;
    }

    public char getMiss() {
        return miss;
    }

    public char getShipwreck() { return shipwreck; }
}
