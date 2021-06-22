package academy.mindswap;
public class Main {
    public static void main(String[] args) {
        Board player1Board = new Board();
        Board player2Board = new Board();
        Game game = new Game();

        game.createGame(player1Board, player2Board);
    }
}
