package academy.mindswap;

public class Game {

    private Player player1;
    private Player player2;
    private int numberOfTurns;


    public Game() {
        player1 = new Player();
        player2 = new Player();
    }

    public void start() {
        System.out.println("Player1 boards");
        player1.createBoards();
        player1.printBoard();
        System.out.println("Player2 boards");
        player2.createBoards();
        player2.printBoard();
    }

//    public void compareBoards(int X, int Y) {
//        System.out.println("TURN #" + ++numberOfTurns);
//        if (player1Board.getMatrix()[X][Y] == 'H') {
//            player1Board.hit(X,Y);
//            player2EmptyBoard.hit(X,Y);
//            System.out.println("Player 1 board");
//            player1Board.printBoard();
//            System.out.println("Player 2 empty board");
//            player2EmptyBoard.printBoard();
//        } else {
//            player1Board.miss(X,Y);
//            player2EmptyBoard.miss(X,Y);
//            System.out.println("Player 1 board");
//            player1Board.printBoard();
//            System.out.println("Player 2 empty board");
//            player2EmptyBoard.printBoard();
//
//        }
//    }

}
