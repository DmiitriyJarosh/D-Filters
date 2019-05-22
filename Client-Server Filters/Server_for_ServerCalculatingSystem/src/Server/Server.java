package Server;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private List<String> filtersList;
    private Receiver receiver;
    private final String filename = "config.ini";
    private ExecutorService clientPool;
    private ExecutorService imagePool;

    Server() {
        clientPool = Executors.newFixedThreadPool(2);
        imagePool = Executors.newFixedThreadPool(2);
        receiver = new Receiver(this);
        filtersList = loadFiltersList();
        receiver.run();
    }

    public List<String> loadFiltersList() {
        List<String> list = new LinkedList<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            while (in.ready()) {
                list.add(in.readLine());
            }
        } catch (Exception e) {
            System.out.println("Config file not found!!!");
        }
        return list;
    }

    public List<String> getFiltersList() {
        return filtersList;
    }

    public void addImageModifier(ImageModifier imageModifier) {
        imagePool.submit(imageModifier);
    }

    public void addClient(Socket socket) {
        new Thread(new ClientDialog(socket, this)).start();
    }
}
