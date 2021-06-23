package academy.mindswap;

// import academy.mindswap.util.RandomGenerator; FIXME

public class Board {
    private int width;
    private int height;
    private char[][] matrix;
    private char water = '~';
    private char ship = 'H';
    private char hit = 'X';
    private char miss = 'O';
    private int numberOfShips;
    private int numberOfShipsSunk;
    // private Ship[] ships; FIXME

    public Board() {
        width = 10;
        height = 10;
        matrix = new char[width][height];
        numberOfShips = 4;
        numberOfShipsSunk = 0;
        // ships = new Ship[numberOfShips];
    }

    public void createBoard() {

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = water;
            }
        }
    }

//    public void placeShips() {
//
//        for (int i = 0; i < numberOfShips; i++) {
//            int randomCoordinateWidth = RandomGenerator.randomNumber(matrix.length);
//            int randomCoordinateHeight = RandomGenerator.randomNumber(matrix.length);
//
//            if (matrix[randomCoordinateWidth][randomCoordinateHeight] == water) {
//                matrix[randomCoordinateWidth][randomCoordinateHeight] = ship;
//                continue;
//            }
//            i--;
//        }
//    }

    public void printBoard() {
        System.out.print("   ");
        for (int i = 0; i < matrix.length; i++) {
            System.out.print((i + 1) + " ");
        }
        System.out.println();
        for (int i = 0; i < matrix.length; i++) {
            if (i < 9) {
                System.out.print(" ");
            }
            System.out.print((i + 1) + " ");
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void hit(int row, int col) {
        matrix[row][col] = hit;
    }

    public void miss(int row, int col) {
        matrix[row][col] = miss;
    }

    public char[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(char[][] matrix) {
        this.matrix = matrix;
    }
}
