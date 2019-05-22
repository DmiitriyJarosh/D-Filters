package Server;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ClientDialog implements Runnable {

    private Socket socket;
    private DataInputStream in;
    private Server server;
    private DataOutputStream out;
    private ImageModifier imageModifier;

    ClientDialog(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        imageModifier = null;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFiltersList(List<String> list) {
        sendMSG(Integer.toString(list.size()));
        try {
            in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String res = "";
        for (String s : list) {
            res += s + "|";

        }
        sendMSG(res);
    }

    private void sendImage(BufferedImage image) {
        try {
            System.out.println("Start sending image");
            sendMSG(Integer.toString(image.getWidth()));
            in.readUTF();
            sendMSG(Integer.toString(image.getHeight()));
            in.readUTF();
            sendMSG(Integer.toString(image.getType()));
            in.readUTF();
            //System.out.println("1");
            String s;
            for (int i = 0; i < image.getWidth(); i++) {
                s = "";
                for (int j = 0; j < image.getHeight(); j++) {
                    s += Integer.toString(image.getRGB(i, j)) + "|";
                    //sendMSG(Integer.toString(image.getRGB(i, j)));

                }
                sendMSG(s);
                if (in.readUTF().equals("BREAK")) {
                    System.out.println("break sending image");
                    return;
                }
            }
            System.out.println("finished sending image");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage receiveImage() throws IOException {
        BufferedImage image = null;
        try {
            int width = Integer.parseInt(in.readUTF());
            sendMSG("Ready!");
            int height = Integer.parseInt(in.readUTF());
            sendMSG("Ready!");
            int type = Integer.parseInt(in.readUTF());
            sendMSG("Ready!");
            image = new BufferedImage(width, height, type);
            System.out.println("receive start");
            for (int i = 0; i < width; i++) {
                String tmp = in.readUTF();
                if (!tmp.equals("BREAK")) {
                    String[] pixels = tmp.split("\\|");
                    for (int j = 0; j < height; j++) {
                        image.setRGB(i, j, Integer.parseInt(pixels[j]));
                    }
                    sendMSG("Ready");
                } else {
                    System.out.println("receive break");
                    return null;
                }


            }
            System.out.println("receive finished");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


    private void sendMSG(String MSG) {
        try {
            out.writeUTF(MSG);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        List<String> list = server.getFiltersList();
        sendFiltersList(list);
        String line = null;
        while (true) {
            try {
                if (in.available() != 0 || imageModifier == null) {
                    line = in.readUTF();
                    switch (line) {
                        case "START":
                            sendMSG("Ready!");
                            String filter = in.readUTF();
                            sendMSG("Ready!");
                            BufferedImage image = receiveImage();
                            if (image == null) {
                                continue;
                            }
                            imageModifier = new ImageModifier(image, filter);
                            server.addImageModifier(imageModifier);
                            break;
                        case "BREAK":
                            if (imageModifier != null) {
                                imageModifier.stop();
                                imageModifier = null;
                            }
                            break;
                        case "DISCONNECT":
                            System.out.println("Disconnected");
                            if (imageModifier != null) {
                                imageModifier.stop();
                            }
                            return;
                    }
                }
                if (imageModifier != null) {
                    sendMSG("PROGRESS");
                    switch (in.readUTF()) {
                        case "DISCONNECT":
                            System.out.println("Disconnected");
                            imageModifier.stop();
                            return;
                        case "BREAK":
                            System.out.println("Break");
                            imageModifier.stop();
                            imageModifier = null;
                            continue;
                    }
                    sendMSG(Double.toString(imageModifier.getProgress()));
                    switch (in.readUTF()) {
                        case "DISCONNECT":
                            System.out.println("Disconnected");
                            imageModifier.stop();
                            return;
                        case "BREAK":
                            System.out.println("Break");
                            imageModifier.stop();
                            imageModifier = null;
                            continue;
                    }
                    if (imageModifier.getProgress() == 1) {
                        sendMSG("FINISHED");
                        in.readUTF();
                        sendImage(imageModifier.getImage());
                        imageModifier = null;
                    }
                }
            } catch (Exception e) {
                return;
                //e.printStackTrace();
            }
        }
    }
}
