package academy.mindswap;

import academy.mindswap.ships.Ship;
import academy.mindswap.ships.ShipType;
import static academy.mindswap.util.Messages.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
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
        ShipType[] ships;
        int numberOfShips = 4;

        public PlayerHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            myBoard = new Board();
            enemyBoard = new Board();
            ships = new ShipType[]{ShipType.DESTROYER, ShipType.SUBMARINE, ShipType.BATTLESHIP, ShipType.CARRIER};

            }


        @Override
        public void run() {
            prepareBattle();

            //TODO: Major method to include below methods
            // (startBattle()

        }

        public void prepareBattle() {
            myBoard.createBoard();
            sendBoard(myBoard);
            placeShips();
            //TODO: sendBoards() (from below) here ?
            // use Arrays.deepToString ?
           // placeShips();
        }

        // TODO: Maybe this isn't needed
        public void createBoards() {
            myBoard.createBoard();
            enemyBoard.createBoard();
        }

        public void sendBoard(Board board) {
            StringBuffer stringBuffer = new StringBuffer();

            stringBuffer.append("   ");
            for (int i = 0; i < board.getMatrix().length; i++) {
                stringBuffer.append((i + 1) + " ");
            }
            stringBuffer.append("\n");
            for (int i = 0; i < board.getMatrix().length; i++) {
                if (i < 9) {
                    stringBuffer.append(" ");
                }
                stringBuffer.append((i + 1) + " ");
                for (int j = 0; j < board.getMatrix()[i].length; j++) {
                    stringBuffer.append(board.getMatrix()[i][j] + " ");
                }
                stringBuffer.append("\n");
            }
            stringBuffer.append("\n");

            out.print(stringBuffer);
            out.flush();
        }

        public void placeShip(ShipType shipType) {
            out.println("Place your Destroyer (size: 2)");
            try {
                in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void placeShips() {

            int row;
            for (int i = 0; i < ships.length; i++) {
                out.println("Place your " + ships[i].getShipName() + " (size: " + ships[i].getShipLength() + ").");
                row = askRow();
                System.out.println(row);
            }


        }

        private int askRow() {
            int userInputInt = 0;
            String userInputString;
            try {
                out.println(INSERT_ROW);

                userInputString = in.readLine();

                try {
                    userInputInt = Integer.parseInt(userInputString);
                    if (userInputInt < 0 || userInputInt > 10) {
                        out.println("TRY ERROR");
                        out.println(INVALID_ROW);
                        askRow();
                    }
                } catch (NumberFormatException e) {
                    out.println("Exception!");
                    out.println(INVALID_ROW);
                    askRow();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return userInputInt;
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







