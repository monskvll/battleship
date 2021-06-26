package academy.mindswap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static academy.mindswap.util.Messages.*;

/**
 * Player class which contains a Buffered Reader for Server input, a Buffered Reader for User input, a PrintWriter for
 * User output and a client Socket for connectivity. Contains its constructor, a start method, a console input reading
 * method and an input writing method.
 */
public class Player {

    private BufferedReader in;
    private BufferedReader userInput;
    private PrintWriter out;
    private Socket clientSocket;

    /**
     * Player main method.
     * @param args main args
     */
    public static void main(String[] args) {
        Player player = new Player();
        player.start();
    }

    /**
     * Player starting method, which creates its Socket, Buffered Readers and Buffered Writer, allowing for inputs to be
     * sent/received to/from the server.
     */
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

    /**
     * Console input reading method.
     * @return console input
     */
    public String readConsoleInput() {
        String input = "";

        try {

            input = userInput.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }

    /**
     * Input writing method.
     * @param input
     */
    public void writeInput(String input) {

        out.println(input);
    }
}