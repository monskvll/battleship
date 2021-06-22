package academy.mindswap;

public class Board {

    private int width;
    private int height;
    private int[][] matrix = new int[10][10];

    public Board() {
        width = 10;
        height = 10;
    }

    public void createBoard() {
        matrix = new int[][]{
                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
        };

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}

