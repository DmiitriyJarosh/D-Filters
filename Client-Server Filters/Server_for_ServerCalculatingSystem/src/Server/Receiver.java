package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver {

    private Server server;
    private ServerSocket serverSocket;
    private static final int port = 10001;

    public Receiver(Server server) {
        this.server = server;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                server.addClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
