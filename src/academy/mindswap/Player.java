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
        out.println(input);
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