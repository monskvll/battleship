package academy.mindswap;

import academy.mindswap.util.RandomGenerator;

import static academy.mindswap.util.Messages.*;
import static academy.mindswap.util.Symbols.*;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;
import java.io.*;

/**
 * Server class which contains full game functionality, connectivity and concurrency.
 */
public class Server {

    public static int NUMBER_OF_MAX_CLIENTS = 2;
    public static int NUMBER_OF_MAX_HITS = 14;
    static int clientsConnected = 0;
    private static CopyOnWriteArrayList<PlayerHandler> playerList;
    private int playersReady = 0;
    private int roundNumber = 1;

    /**
     * Server main method.
     *
     * @param args main args
     */
    public static void main(String[] args) {
        int PORT = 8080;

        Server server = new Server();
        server.start(PORT);
    }

    /**
     * Server starting method which creates its player list CopyOnWriteArrayList, Server Socket, Executor Service for
     * threading, Player Handler, allowing for Server-Player connectivity
     *
     * @param port port in use
     */
    public void start(int port) {

        playerList = new CopyOnWriteArrayList<>();

        ServerSocket serverSocket;

        System.out.println(SERVER_STARTING);

        try {

            serverSocket = new ServerSocket(port);

            ExecutorService cachedPool = Executors.newCachedThreadPool();

            while (!serverSocket.isClosed()) {

                while (clientsConnected < NUMBER_OF_MAX_CLIENTS) {
                    Socket socket = serverSocket.accept();

                    System.out.printf(USER_CONNECTED, socket.getInetAddress());

                    PlayerHandler playerHandler = new PlayerHandler(socket);
                    playerList.add(playerHandler);

                    cachedPool.submit(playerHandler);

                    clientsConnected++;
                }
            }
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    /**
     * Fight method which contains starting player randomizer, player number setter, number of max hits checker,
     * fire method (which entails combat functionality), winner/loser checker and player disconnection forcing.
     *
     * @throws IOException
     */
    public void fight() throws IOException {

        char result;
        char result2;

        PlayerHandler player1;
        PlayerHandler player2;

        // Starting player randomizer + player number setter
        int playerChance = RandomGenerator.randomNumberMinMax(0, 1);

        if (playerChance == 0) {
            player1 = playerList.get(0);
            player2 = playerList.get(1);
        } else {
            player1 = playerList.get(1);
            player2 = playerList.get(0);
        }

        // Number of max hits checker and fire method (combat functionality)
        while (player1.numberOfTimesHit < NUMBER_OF_MAX_HITS && player2.numberOfTimesHit < NUMBER_OF_MAX_HITS) {

            player1.out.printf(ROUND_NUMBER, roundNumber);
            player2.out.printf(ROUND_NUMBER, roundNumber);

            roundNumber++;

            do {

                player2.sendMessage(WAITING_FOR_OPPONENT);

                result = fire(player1, player2);

            } while ((result == SHIPWRECK || result == HIT) && player2.numberOfTimesHit < NUMBER_OF_MAX_HITS);

            if (player2.numberOfTimesHit < NUMBER_OF_MAX_HITS) {

                do {
                    player1.sendMessage(WAITING_FOR_OPPONENT);

                    result2 = fire(player2, player1);

                } while ((result2 == SHIPWRECK || result2 == HIT) && player1.numberOfTimesHit < NUMBER_OF_MAX_HITS);
            }
        }

        checkWinnerAndLoser(player1, player2);
        disconnectPlayers();
    }

    /**
     * Fire method which contains attacking and defending methods, repeated position attempts checker and board sender
     * (which updates them).
     *
     * @param attacker attacking player
     * @param defender defending player
     * @return char result from the attempted attack position
     * @throws IOException
     */
    public char fire(PlayerHandler attacker, PlayerHandler defender) throws IOException {

        char result;

        do {
            attacker.attack();
            result = defender.sufferAttack(attacker.currentRow, attacker.currentCol);

            switch (result) {
                case MISS:
                    attacker.changeEnemyBoard(result);
                    break;
                case HIT:
                    if (!checkIfShipDestroyed(defender, attacker)) {
                        attacker.changeEnemyBoard(result);
                    }
                    break;
                case INVALID:
                    attacker.sendMessage(INVALID_PLAYER_PLAY);
                    break;
                default:
                    break;
            }

        } while (result == INVALID);

        attacker.sendBoards();
        defender.sendBoards();

        return result;
    }


    /**
     * Ship checker method, which checks if ship has been sunk for a given player.
     *
     * @param defender defending player
     * @param attacker attacking player
     * @return boolean for whether a ship has been destroyed or not
     */
    public boolean checkIfShipDestroyed(PlayerHandler defender, PlayerHandler attacker) {
        int shipIndex = -1;

        for (Point key : defender.shipsCoordinates.keySet()) {
            if (key.getX() == attacker.currentRow && key.getY() == attacker.currentCol) {
                shipIndex = defender.shipsCoordinates.remove(key);
            }
        }

        if (!defender.shipsCoordinates.containsValue(shipIndex)) {
            int keyX;
            int keyY;
            for (Point key : defender.shipsCoordinatesCopy.keySet()) {
                if (defender.shipsCoordinatesCopy.get(key) == shipIndex) {
                    keyX = (int) key.getX();
                    keyY = (int) key.getY();
                    attacker.enemyBoard.getMatrix()[keyX][keyY] = SHIPWRECK;
                    defender.myBoard.getMatrix()[keyX][keyY] = SHIPWRECK;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Winner/loser checker method.
     *
     * @param player1 first player
     * @param player2 second player
     */
    public void checkWinnerAndLoser(PlayerHandler player1, PlayerHandler player2) {
        if (player1.numberOfTimesHit == NUMBER_OF_MAX_HITS) {
            player1.sendMessage(PLAYER_LOSS);
            player2.sendMessage(PLAYER_WIN);
        } else {
            player1.sendMessage(PLAYER_WIN);
            player2.sendMessage(PLAYER_LOSS);
        }
    }

    /**
     * Battle starting method, which checks if enough player have connected, initiating the fight, if so.
     *
     * @param player player
     * @throws IOException
     */
    public void startBattle(PlayerHandler player) throws IOException {
        if (playersReady == NUMBER_OF_MAX_CLIENTS) {
            fight();
        }

        player.sendMessage(WAITING_FOR_OPPONENT);
    }

    /**
     * Player disconnection forcing method, which closes all player sockets when the game ends.
     */
    public void disconnectPlayers() {
        try {
            playerList.get(0).clientSocket.close();
            playerList.get(1).clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * PlayerHandler inner class, which implements Runnable, which contains a client Socket, a Buffered Reader,
     * a PrintWriter, both boards (the player's and the enemy's empty mirror board), ships array, two maps with ship
     * coordinates, current row/column/direction being chosen and the number of times that ships have been hit.
     */
    class PlayerHandler implements Runnable {

        Socket clientSocket;
        PrintWriter out;
        BufferedReader in;
        Board myBoard;
        Board enemyBoard;
        ShipType[] ships;
        Map<Point, Integer> shipsCoordinates;
        Map<Point, Integer> shipsCoordinatesCopy;
        int currentRow;
        int currentCol;
        int currentDir;
        int numberOfTimesHit;

        /**
         * PlayerHandler constructor which contains a client Socket and creates the necessary properties.
         *
         * @param clientSocket client socket
         * @throws IOException
         */
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
         * Waiting interval applying method, which gives the illusion of waiting without hanging the thread
         *
         * @param seconds number of seconds to wait
         */
        public void wait(int seconds) {
            long time0, time1;
            time0 = System.currentTimeMillis();
            do {
                time1 = System.currentTimeMillis();
            }
            while (time1 - time0 < seconds * 1000L);
        }

        /**
         * Override for the PlayerHandler run method, which starts the battle preparation and start methods, with certain
         * intervals between them along with necessary messages.
         */
        @Override
        public void run() {

            try {
                wait(2);
                sendMessage(WELCOME);
                wait(3);
                sendMessage(INSTRUCTIONS);
                wait(3);
                prepareBattle();
                wait(2);
                startBattle(this);
            } catch (IOException e) {
                try {
                    clientSocket.close();
                } catch (IOException i) {
                    i.printStackTrace();
                }
            }
        }

        /**
         * Message sending method, which prints a message through the Player Handler writer.
         * @param message message to be sent
         */
        public void sendMessage(String message) {
            out.println(message);
        }

        /**
         * Battle preparation method, which creates both boards (the player's and the enemy's empty mirror board),
         * prints only the player's own board, calls for the ship placement method and increments the number of players ready.
         * @throws IOException
         */
        public void prepareBattle() throws IOException {
            myBoard.createBoard();
            enemyBoard.createBoard();
            sendBoard(myBoard);
            placeShips();
            playersReady++;
        }

        /**
         * Board printing grouping method, which prints both boards (the player's and the enemy's empty mirror board).
         */
        public void sendBoards() {
            sendMessage(PLAYER_BOARD);
            sendBoard(myBoard);
            sendMessage(DIVISOR);
            sendMessage(ENEMY_BOARD);
            sendBoard(enemyBoard);
        }

        /**
         * Board printing method, which prints a board using a String Buffer.
         * @param board game board
         */
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

        /**
         * Ship placement method, which calls for the row/column/direction asking methods, while checking if all ships
         * have already been placed, saves the ship coordinates (draws the ship) and updates the player's board.
         * @throws IOException
         */
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
         * Ship placement checker method, which validates the coordinates given by the user.
         * @param shipType type of ship
         * @return
         */
        public boolean checkShipPlacement(ShipType shipType) {

            if (myBoard.getMatrix()[currentRow][currentCol] != WATER) {
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
                        if (myBoard.getMatrix()[currentRow][currentCol + i] != WATER) {
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
                        if (myBoard.getMatrix()[currentRow + i][currentCol] != WATER) {
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
         * Ship drawing method, which saves its coordinates and effectively draws it on the board, while checking
         * the current direction.
         * @param shipType type of ship
         * @param shipIndex value of the ship coordinates map
         */
        public void drawShip(ShipType shipType, Integer shipIndex) {

            switch (currentDir) {
                case 0:
                    for (int i = 0; i < shipType.getShipLength(); i++) {
                        myBoard.getMatrix()[currentRow][currentCol + i] = SHIP;

                        Point point = new Point(currentRow, currentCol + i);
                        shipsCoordinates.put(point, shipIndex);
                        shipsCoordinatesCopy.put(point, shipIndex);
                    }
                    break;

                case 1:
                    for (int i = 0; i < shipType.getShipLength(); i++) {
                        myBoard.getMatrix()[currentRow + i][currentCol] = SHIP;

                        Point point = new Point(currentRow + i, currentCol);
                        shipsCoordinates.put(point, shipIndex);
                        shipsCoordinatesCopy.put(point, shipIndex);
                    }
                    break;
            }
        }

        /**
         * Row asking methods, which asks for the X position for ship placement and player attacks.
         * @throws IOException
         */
        private void askRow() throws IOException {

            int userInputInt;

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

        /**
         * Column asking methods, which asks for the Y position for ship placement and player attacks.
         * @throws IOException
         */
        public void askCol() throws IOException {

            int userInputInt;

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

        /**
         * Direction asking methods, which asks for the direction for ship placement and player attacks.
         * @throws IOException
         */
        public void askDir() throws IOException {

            int userInputInt;

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

        /**
         * Attacking method, which asks for attack coordinates (X and Y position).
         * @throws IOException
         */
        public void attack() throws IOException {
            askRow();
            askCol();
        }

        /**
         * Attack receiving method, which checks which board coordinates was hit and saves the resulting character to
         * print it on the board.
         * @param row X position
         * @param col Y position
         * @return hit coordinate char
         */
        public char sufferAttack(int row, int col) {

            char pointToHit = myBoard.getMatrix()[row][col];

            if (pointToHit == WATER) {

                myBoard.getMatrix()[row][col] = MISS;
                pointToHit = MISS;


            } else if (pointToHit == SHIP) {

                myBoard.getMatrix()[row][col] = HIT;
                numberOfTimesHit++;
                pointToHit = HIT;

            } else if (pointToHit == MISS || pointToHit == HIT || pointToHit == SHIPWRECK) {

                pointToHit = INVALID;
            }
            return pointToHit;
        }

        /**
         * Enemy board changing method, which changes the hit coordinate.
         * @param result hit coordinate char
         */
        public void changeEnemyBoard(char result) {
            enemyBoard.getMatrix()[currentRow][currentCol] = result;
        }
    }
}