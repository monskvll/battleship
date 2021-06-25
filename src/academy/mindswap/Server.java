package academy.mindswap;

import academy.mindswap.ships.ShipType;
import academy.mindswap.util.RandomGenerator;

import static academy.mindswap.util.Messages.*;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;
import java.io.*;

public class Server {

    public static int NUMBER_OF_MAX_CLIENTS = 2;
    static int clientsConnected = 0;
    private static CopyOnWriteArrayList<PlayerHandler> playerList;
    private int playersReady = 0;
    private int roundNumber = 1;

    public static void main(String[] args) {
        int PORT = 8080;

        Server server = new Server();
        server.listen(PORT);
    }

    public void listen(int port) {

        playerList = new CopyOnWriteArrayList<>();

        ServerSocket serverSocket = null;

        System.out.println(SERVER_STARTING);

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
                }
            }
            System.out.println("Server socket closed: " + serverSocket.isClosed());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fight() {

        char invalidPlayChar = 'E';
        char successiveHitChar = 'X';
        char result;
        char result2;

        PlayerHandler player1 = null;
        PlayerHandler player2 = null;

        int playerChance = RandomGenerator.randomNumberMinMax(0, 1);

        if (playerChance == 0) {
            player1 = playerList.get(0);
            player2 = playerList.get(1);
        } else {
            player1 = playerList.get(1);
            player2 = playerList.get(0);
        }

        while (player1.numberOfTimesHit < 14 && player2.numberOfTimesHit < 14) {

            player1.out.printf(ROUND_NUMBER, roundNumber);
            player2.out.printf(ROUND_NUMBER, roundNumber);

            roundNumber++;

            do {

                player2.sendMessage(WAITING_FOR_OPPONENT);

                do {
                    player1.attack();
                    result = player2.sufferAttack(player1.currentRow, player1.currentCol);
                    if (result == invalidPlayChar) {
                        player1.sendMessage(INVALID_PLAYER_PLAY);
                    }
                } while (result == invalidPlayChar);

                player1.changeEnemyBoard(result);
                player1.sendBoards();
                player2.sendBoards();

            } while (result == successiveHitChar && player2.numberOfTimesHit < 14);

            if (player2.numberOfTimesHit < 14) {

                do {

                    player1.sendMessage(WAITING_FOR_OPPONENT);

                    do {
                        player2.attack();
                        result2 = player1.sufferAttack(player2.currentRow, player2.currentCol);
                        if (result2 == invalidPlayChar) {
                            player2.sendMessage(INVALID_PLAYER_PLAY);
                        }
                    } while (result2 == invalidPlayChar);

                    player2.changeEnemyBoard(result2);
                    player2.sendBoards();
                    player1.sendBoards();

                } while (result2 == successiveHitChar && player1.numberOfTimesHit < 14);
            }
        }

        checkWinnerAndLoser(player1, player2);
    }

    public void checkWinnerAndLoser(PlayerHandler player1, PlayerHandler player2) {
        if (player1.numberOfTimesHit == 14) {
            player1.sendMessage(PLAYER_LOSS);
            player2.sendMessage(PLAYER_WIN);
        } else {
            player1.sendMessage(PLAYER_WIN);
            player2.sendMessage(PLAYER_LOSS);
        }
    }

    public void checkIfPlayersReady(PlayerHandler player) {
        if (playersReady == NUMBER_OF_MAX_CLIENTS) {
            fight();
            return;
        }
        player.sendMessage(WAITING_FOR_OPPONENT);
    }

    class PlayerHandler implements Runnable {

        Socket clientSocket;
        PrintWriter out = null;
        BufferedReader in = null;

        Board myBoard;
        Board enemyBoard;
        ShipType[] ships;
        Map<Point, Integer> shipsCoordinates;
        int currentRow;
        int currentCol;
        int currentDir;
        int numberOfTimesHit;

        public PlayerHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            myBoard = new Board();
            enemyBoard = new Board();
            ships = new ShipType[]{ShipType.DESTROYER, ShipType.SUBMARINE, ShipType.BATTLESHIP, ShipType.CARRIER};
            shipsCoordinates = new ConcurrentHashMap<>();
        }

        @Override
        public void run() {
            sendMessage(WELCOME_INSTRUCTIONS);
            prepareBattle();
            // sendBoards();
            checkIfPlayersReady(this);
        }

        public void sendMessage(String message){
            out.println(message);
        }

        public void prepareBattle() {
            myBoard.createBoard();
            enemyBoard.createBoard();
            sendBoard(myBoard);
            placeShips();
            playersReady++;
        }

        public void sendBoards() {
            sendMessage(PLAYER_BOARD);
            sendBoard(myBoard);
            sendMessage(DIVISOR);
            sendMessage(ENEMY_BOARD);
            sendBoard(enemyBoard);
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

        public void placeShips() {

            for (int i = 0; i < ships.length; i++) {
                out.printf(PLACE_SHIP, ships[i].getShipName(), ships[i].getShipLength());

                do {
                    askRow();
                    askCol();
                    askDir();
                }
                while (!checkShipPlacement(ships[i]));

                drawShip(ships[i], i);
                sendBoard(myBoard);
            }
        }

        public boolean checkShipPlacement(ShipType shipType) {

            if (myBoard.getMatrix()[currentRow][currentCol] != myBoard.getWater()) {
                sendMessage(INVALID_POSITION);
                return false;
            }

            switch (currentDir) {
                case 0:
                    if (currentCol + shipType.getShipLength() > 9) {
                        sendMessage(OUT_OF_BORDERS);
                        return false;
                    }

                    for (int i = 1; i < shipType.getShipLength(); i++) {
                        if (myBoard.getMatrix()[currentRow][currentCol + i] != myBoard.getWater()) {
                            sendMessage(INVALID_POSITION);
                            return false;
                        }
                    }
                    return true;

                case 1:
                    if (currentRow + shipType.getShipLength() > 9) {
                        sendMessage(OUT_OF_BORDERS);
                        return false;
                    }

                    for (int i = 1; i < shipType.getShipLength(); i++) {
                        if (myBoard.getMatrix()[currentRow + i][currentCol] != myBoard.getWater()) {
                            sendMessage(INVALID_POSITION);
                            return false;
                        }
                    }
                    return true;

                default:
                    return false;
            }
        }

        public void drawShip(ShipType shipType, Integer shipIndex) {

            switch (currentDir) {
                case 0:
                    for (int i = 0; i < shipType.getShipLength(); i++) {
                        myBoard.getMatrix()[currentRow][currentCol + i] = myBoard.getShip();

                        shipsCoordinates.put(new Point(currentRow,currentCol + i), shipIndex);
                        System.out.println(shipsCoordinates);
                    }
                    break;

                case 1:
                    for (int i = 0; i < shipType.getShipLength(); i++) {
                        myBoard.getMatrix()[currentRow + i][currentCol] = myBoard.getShip();
                        shipsCoordinates.put(new Point(currentRow + i,currentCol), shipIndex);
                    }
                    break;
            }
        }

        public void checkIfShipDestroyed() {

        }

        private void askRow() {

            int userInputInt = 0;

            sendMessage(INSERT_ROW);

            try {
                String userInputString = in.readLine();
                userInputInt = Integer.parseInt(userInputString);

                if (userInputInt > 0 && userInputInt <= 10) {
                    currentRow = userInputInt - 1; // -1 to ensure array/console print fidelity
                    return;
                }
                sendMessage(INVALID_ROW);
                askRow();

            } catch (NumberFormatException e) {
                sendMessage(INVALID_ROW);
                askRow();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }

        public void askCol() {

            int userInputInt = 0;

            sendMessage(INSERT_COL);

            try {
                String userInputString = in.readLine();
                userInputInt = Integer.parseInt(userInputString);

                if (userInputInt > 0 && userInputInt <= 10) {
                    currentCol = userInputInt - 1; // -1 to ensure array/console print fidelity
                    return;
                }
                sendMessage(INVALID_COL);
                askCol();

            } catch (NumberFormatException e) {
                sendMessage(INVALID_COL);
                askCol();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void askDir() {

            int userInputInt = 0;

            sendMessage(INSERT_DIR);

            try {
                String userInputString = in.readLine();
                userInputInt = Integer.parseInt(userInputString);

                if (userInputInt == 0 || userInputInt == 1) {
                    currentDir = userInputInt;
                    return;
                }
                sendMessage(INVALID_DIR);
                askDir();

            } catch (NumberFormatException e) {
                sendMessage(INVALID_DIR);
                askDir();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void attack() {
            askRow();
            askCol();
        }

        public char sufferAttack(int row, int col) {

            char pointToHit = myBoard.getMatrix()[row][col];
            char invalidPlayChar = 'E';

            if (pointToHit == myBoard.getWater()) {

                myBoard.getMatrix()[row][col] = myBoard.getMiss();
                pointToHit = myBoard.getMiss();

            } else if (pointToHit == myBoard.getShip()) {

                myBoard.getMatrix()[row][col] = myBoard.getHit();
                numberOfTimesHit++;
                pointToHit = myBoard.getHit();

            } else if (pointToHit == myBoard.getMiss() || pointToHit == myBoard.getHit()) {

                pointToHit = invalidPlayChar;
            }

            return pointToHit;
        }

        public void changeEnemyBoard(char result) {
            enemyBoard.getMatrix()[currentRow][currentCol] = result;
        }
    }
}