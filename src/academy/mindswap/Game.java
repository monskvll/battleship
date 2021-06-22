package academy.mindswap;

public class Game {



    public Game() {

    }

    public void createGame(Board player1Board, Board player2Board) {
        player1Board.createBoard();
        System.out.println("=====");
        player2Board.createBoard();
    }


}
