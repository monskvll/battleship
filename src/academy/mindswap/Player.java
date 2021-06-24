package academy.mindswap;

import java.io.*;
import java.net.Socket;

public class Player {

    private String name;
    private BufferedReader userInput;
    private PrintWriter out;
    private Socket clientSocket;

    public Player() {

    }

    public static void main(String[] args) {
        Player player = new Player();
        player.start();
    }

    public void start() {

        try {
            clientSocket = new Socket("localhost", 8080);

            if (clientSocket.isConnected()) {
                System.out.println("Connected.");
            }

            userInput = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            Thread serverReaderThread = new Thread(new ServerReader());
            serverReaderThread.start();

            while (!clientSocket.isClosed()) {
                String input = readUserInput();
                writeInput(input);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readUserInput() {
        String input = "";

        try {
            input = userInput.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }

    public void writeInput(String input) {
        out.println();
    }

    public void pickRow() {
        try {
            System.out.println("Pick row (1-10).");

            int inputRow = Integer.parseInt(userInput.readLine());

            if (inputRow < 0 || inputRow > 10) {
                System.out.println("Invalid row.");
                pickRow();
            }

            out.println(inputRow);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pickCol() {
        try {
            System.out.println("Pick column (1-10).");

            int inputCol = Integer.parseInt(userInput.readLine());

            if (inputCol < 0 || inputCol > 10) {
                System.out.println("Invalid row.");
                pickCol();
            }

            out.println(inputCol);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pickDir() {
        try {
            System.out.println("Pick direction (0 = horizontal, 1 = vertical).");

            int inputDir = Integer.parseInt(userInput.readLine());

            if (inputDir < 0 || inputDir > 1) {
                System.out.println("Invalid direction.");
                pickDir();
            }

            out.println(inputDir);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pickShipLocation() {
            pickRow();
            pickCol();
            pickDir();
    }

    class ServerReader implements Runnable {

        private BufferedReader in;

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line = "";
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}