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

    public void start()  {

        try {
            clientSocket = new Socket("localhost", 8080);

            if(clientSocket.isConnected()) {
                System.out.println("connected");
            }

            userInput = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            Thread serverReaderThread = new Thread(new ServerReader());
            serverReaderThread.start();

            while(!clientSocket.isClosed()) {
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

    public void placeShips() {

        try {
            System.out.println("Pick row.");
            String inputRow = userInput.readLine();

            System.out.println("Pick column.");
            String inputColumn = userInput.readLine();

            System.out.println("Pick direction.");
            String inputDirection = userInput.readLine();

        } catch (java.io.IOException e){
            e.printStackTrace();
        }
    }

//    public boolean addShip(int shipLength, int row, int col, char direction) {
//
//        // TODO:
//        //  check values row, col,(1-10) direction (H/V)
//        // check availability
//        // if available: change to H
//        // if not, ask again
//
//
//
//    }

    private class ServerReader implements Runnable{

        private BufferedReader in;

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line = "";
                while((line = in.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
