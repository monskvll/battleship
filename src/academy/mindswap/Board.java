package academy.mindswap;

import static academy.mindswap.util.Symbols.WATER;

/**
 * Board class which contains board width, height and matrix, along with Board constructor,
 * board creating method and matrix getter.
 */
public class Board {
    private final char[][] matrix;

    /**
     * Board constructor.
     */
    public Board() {
        int width = 10;
        int height = 10;
        matrix = new char[width][height];
    }

    /**
     * Board creating method, resulting in a matrix that fills it with the specified Symbol char.
     */
    public void createBoard() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = WATER;
            }
        }
    }

    /**
     * Matrix getter.
     * @return matrix
     */
    public char[][] getMatrix() {
        return matrix;
    }
}
