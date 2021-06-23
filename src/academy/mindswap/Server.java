package academy.mindswap;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int PORT = 8080;


    public static void main(String[] args) {

        try {
            Server server = new Server();
            server.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void start() throws IOException {

        ServerSocket serverSocket = new ServerSocket(PORT);
        ExecutorService fixedClients = Executors.newFixedThreadPool(2);

        while (serverSocket.isBound()) {

            Socket clientSocket = serverSocket.accept();
            //fixedClients.submit(new ClientHandler(clientSocket));

        }

        fixedClients.shutdown();

    }


    /*private class ClientHandler implements Runnable {

        private Socket clientSocket;
        private PrintWriter out = null;
        private BufferedReader in = null;
        private String name;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;

        }

        @Override
        public void run() {

        }


    }*/
}






