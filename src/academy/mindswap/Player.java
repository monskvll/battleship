package academy.mindswap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static academy.mindswap.util.Messages.*;

public class Player {

    private BufferedReader in;
    private BufferedReader userInput;
    private PrintWriter out;
    private Socket clientSocket;

    public static void main(String[] args) {
        Player player = new Player();
        player.start();
    }

    public void start() {

        try {
            clientSocket = new Socket("localhost", 8080);
            userInput = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String line = "";

            while ((line = in.readLine()) != null) {
                System.out.println(line);
                if (line.equals(INSERT_ROW) || line.equals(INSERT_COL) || line.equals(INSERT_DIR)) {
                    String input = readConsoleInput();
                    writeInput(input);
                }
            }

            clientSocket.close();
            System.exit(0);

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

        out.println(input);
    }
}