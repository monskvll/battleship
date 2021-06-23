package academy.mindswap;

public class Player {

    private Board myBoard;
    private Board enemyBoard;
    private String name;

    public Player() {
        myBoard = new Board();
        enemyBoard = new Board();
    }

    //main - connection

    public void createBoards() {
        myBoard.createBoard();
        enemyBoard.createBoard();
    }

    public void printBoard() {
        myBoard.printBoard();
        enemyBoard.printBoard();
    }

    public void placeShips() {

    }

    public boolean addShip(int shipLength, int row, int col, char direction) {

        // TODO:
        //  check values row, col,(1-10) direction (H/V)
        // check availability
        // if available: change to H
        // if not, ask again



    }

}
