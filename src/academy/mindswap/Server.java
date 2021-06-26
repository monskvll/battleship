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
            serverSocket.close();

            System.out.println("Server socket closed: " + serverSocket.isClosed());

        } catch (IOException e) {
            e.printStackTrace();

        }
    }


    public void fight() throws IOException{

        char successiveHitChar = 'X';
        char successiveShipwreckChar = '@';
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

                result = fire(player1, player2);

            } while ((result == successiveShipwreckChar || result == successiveHitChar) && player2.numberOfTimesHit < 14);

            if (player2.numberOfTimesHit < 14) {

                do {

                    player1.sendMessage(WAITING_FOR_OPPONENT);

                    result2 = fire(player2, player1);

                } while ((result2 == successiveShipwreckChar || result2 == successiveHitChar) && player1.numberOfTimesHit < 14);
            }
        }

        checkWinnerAndLoser(player1, player2);
        disconnectPlayers();
    }

    public char fire(PlayerHandler attacker, PlayerHandler defender) throws IOException {

        char invalidPlayChar = 'E';
        char result;

        do {

            attacker.attack();
            result = defender.sufferAttack(attacker.currentRow, attacker.currentCol);

            switch (result) {
                case 'O':
                    attacker.changeEnemyBoard(result);
                    break;
                case 'X':
                    if(!checkIfShipDestroyed(defender, attacker)) {
                        attacker.changeEnemyBoard(result);
                    }
                    break;
                case 'E':
                    attacker.sendMessage(INVALID_PLAYER_PLAY);
                    break;
                default:
                    break;
            }

        } while (result == invalidPlayChar);

        attacker.sendBoards();
        defender.sendBoards();

        return result;
    }


    /**
     * Checks if ALL ships have been sunk for a given player
     */

    public boolean checkIfShipDestroyed(PlayerHandler suffering, PlayerHandler attacker) {
        int shipIndex = -1;

        for (Point key : suffering.shipsCoordinates.keySet()) {
            if (key.getX() == attacker.currentRow && key.getY() == attacker.currentCol) {
                shipIndex = suffering.shipsCoordinates.remove(key);
            }
        }

        if (!suffering.shipsCoordinates.containsValue(shipIndex)) {
            int keyX = -1;
            int keyY = -1;
            for (Point key : suffering.shipsCoordinatesCopy.keySet()) {
                if (suffering.shipsCoordinatesCopy.get(key) == shipIndex) {
                    keyX = (int) key.getX();
                    keyY = (int) key.getY();
                    attacker.enemyBoard.getMatrix()[keyX][keyY] = attacker.enemyBoard.getShipwreck();
                    suffering.myBoard.getMatrix()[keyX][keyY] = suffering.myBoard.getShipwreck();
                }
            }
            return true;
        }

        return false;
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

    public void startBattle(PlayerHandler player) throws IOException {
        if (playersReady == NUMBER_OF_MAX_CLIENTS) {
            fight();
        }
        player.sendMessage(WAITING_FOR_OPPONENT);
    }

    public void disconnectPlayers() {
        try {
            playerList.get(0).clientSocket.close();
            playerList.get(1).clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class PlayerHandler implements Runnable {

        Socket clientSocket;
        PrintWriter out = null;
        BufferedReader in = null;

        Board myBoard;
        Board enemyBoard;
        ShipType[] ships;
        Map<Point, Integer> shipsCoordinates;
        Map<Point, Integer> shipsCoordinatesCopy;
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
            shipsCoordinatesCopy = new ConcurrentHashMap<>();
        }

        /**
         * Gives the illusion of waiting without hanging the thread (given a number "k" in seconds)
         */

        public void wait(int k){
            long time0, time1;
            time0 = System.currentTimeMillis();
            do{
                time1 = System.currentTimeMillis();
            }
            while (time1 - time0 < k * 1000);
        }


        @Override
        public void run() {

            try {
                wait(2);
                sendMessage(WELCOME);
                wait(3);
                sendMessage(INSTRUCTIONS);
                wait(5);
                prepareBattle();
                wait(2);
                startBattle(this);
            } catch (IOException e) {
                try {
                    clientSocket.close();
                } catch (IOException i) {
                    System.out.println("blablabla");
                }
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void prepareBattle() throws IOException {
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

        public void placeShips() throws IOException {

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

        /**
         * Validates the coordinates given by the user
         */

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


        /**
         * Method to add a Ship to a player's board
         */

        public void drawShip(ShipType shipType, Integer shipIndex) {

            switch (currentDir) {
                case 0:
                    for (int i = 0; i < shipType.getShipLength(); i++) {
                        myBoard.getMatrix()[currentRow][currentCol + i] = myBoard.getShip();

                        Point point = new Point(currentRow, currentCol + i);
                        shipsCoordinates.put(point, shipIndex);
                        shipsCoordinatesCopy.put(point, shipIndex);

                    }
                    break;

                case 1:
                    for (int i = 0; i < shipType.getShipLength(); i++) {
                        myBoard.getMatrix()[currentRow + i][currentCol] = myBoard.getShip();

                        Point point = new Point(currentRow + i, currentCol);
                        shipsCoordinates.put(point, shipIndex);
                        shipsCoordinatesCopy.put(point, shipIndex);
                    }
                    break;
            }
        }

        private void askRow() throws IOException {

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
            }
        }

        public void askCol() throws IOException {

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
            }
        }

        public void askDir() throws IOException {

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
            }
        }

        public void attack() throws IOException {

            askRow();
            askCol();
        }

        public char sufferAttack(int row, int col) {

            char pointToHit = myBoard.getMatrix()[row][col];
            char invalidPlayChar = 'E';

            if (pointToHit == myBoard.getWater()) {

                myBoard.getMatrix()[row][col] = myBoard.getMiss();
                pointToHit = myBoard.getMiss();

                /**
                 * Checks if the given coordinates correspond to a ship from the other player
                 */

            } else if (pointToHit == myBoard.getShip()) {

                myBoard.getMatrix()[row][col] = myBoard.getHit();
                numberOfTimesHit++;
                pointToHit = myBoard.getHit();

            } else if (pointToHit == myBoard.getMiss() || pointToHit == myBoard.getHit() || pointToHit == myBoard.getShipwreck()) {

                pointToHit = invalidPlayChar;
            }
            return pointToHit;
        }

        public void changeEnemyBoard(char result) {
            enemyBoard.getMatrix()[currentRow][currentCol] = result;
        }
    }
}