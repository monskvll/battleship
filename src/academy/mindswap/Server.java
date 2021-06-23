package academy.mindswap;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;

public class Server {

    public static int NUMBER_OF_MAX_CLIENTS = 2;
    static int clientsConnected = 0;
    private static List<PlayerHandler> playerList;

    public static void main(String[] args) {
        int PORT = 8080;

        Server server = new Server();
        server.listen(PORT);
    }

    public void listen(int port) {
        playerList = new LinkedList<>();

        ServerSocket serverSocket = null;

        System.out.println("STARTING SERVER, please wait...");

        try {

                serverSocket = new ServerSocket(port);

                ExecutorService cachedPool = Executors.newCachedThreadPool();

            while (!serverSocket.isClosed()) {

                while (clientsConnected < NUMBER_OF_MAX_CLIENTS) {
                    Socket socket = serverSocket.accept();

                    System.out.println("User connected: " + socket.getInetAddress());

                    PlayerHandler playerHandler = new PlayerHandler(socket);
                    playerList.add(playerHandler);

                    cachedPool.submit(playerHandler);

                    clientsConnected++;
                    System.out.println("test");
                }
            }
            System.out.println("Server socket closed: " + serverSocket.isClosed());

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    class PlayerHandler implements Runnable {

        Socket clientSocket;
        PrintWriter out = null;
        BufferedReader in = null;

        Board myBoard;
        Board enemyBoard;
        String username;
        int numberOfShips = 4;

        public PlayerHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        @Override
        public void run() {

            prepareBattle();

            //TODO: Major method to include below methods
            // (startBattle()

        }

        public void prepareBattle() {
            createBoards();
            //TODO: sendBoards() (from below) here ?
            // use Arrays.deepToString ?
            placeShips();
        }

        public void createBoards() {
            myBoard.createBoard();
            enemyBoard.createBoard();
        }

        public void sendBoards() {

        }

        public void placeShips() {
            try {
                /* TODO: Validations on this side to check if ship can be placed
                * check values row, col,(1-10) direction (H/V)
                * check space availability
                * * if available, change to H (board.ship)
                * * if not, ask again
                */

                in.readLine();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }


    }

//    public static void start(){
//
//        game = new Game(players.get(0), players.get(1));
//
//        try{
//            game.start();
//        }catch(Exception e){
//            System.out.println("ESCREVER ALGUM ERRO DE COMEÇO"); // MUDAR
//        }
//    }



}







    /*public void start() throws IOException {

        ExecutorService fixedClients = Executors.newFixedThreadPool(2);

        while (serverSocket.isBound()) {

            Socket clientSocket = serverSocket.accept();
            //fixedClients.submit(new ClientHandler(clientSocket));

        }

        fixedClients.shutdown();

    }*/


//    private class ClientHandler implements Runnable {
//
//        private Socket clientSocket;
//        private PrintWriter out = null;
//        private BufferedReader in = null;
//        private String name;
//
//        public ClientHandler(Socket clientSocket) {
//            this.clientSocket = clientSocket;
//
//        }
//
//        @Override
//        public void run() {
//
//        }
//
//
//    }







