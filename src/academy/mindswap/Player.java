package academy.mindswap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Player {

    private BufferedReader userInput;
    private PrintWriter out;
    private Socket clientSocket;
    private boolean isPaused;

    public static void main(String[] args) {
        Player player = new Player();
        player.start();
    }

    public void start() {

        try {
            clientSocket = new Socket("localhost", 8080);
            userInput = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            Thread serverReaderThread = new Thread(new ServerReader());

            serverReaderThread.start();

            while (!clientSocket.isClosed()) {
                String input = readConsoleInput();
                writeInput(input);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readConsoleInput() {
        String input = "";

        try {
            input = userInput.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }

    public void writeInput(String input) {
//        if (isPaused) {
//            try {
//                System.out.println("Waiting is working.");
//                wait();
//            } catch (InterruptedException e) {
//                System.out.println("WriteInput Interrupted Exception");
//            }
//        }
        /// NOTIFY WHERE?
        out.println(input);
    }

    class ServerReader implements Runnable {


        @Override
        public void run() {
            BufferedReader in;

            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line = "";
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
//                    if (line.startsWith("Waiting")) {
//                        isPaused = true;
//                        continue;
//                    } else {
//                        isPaused = false;
//                    }
                }
                clientSocket.close();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}