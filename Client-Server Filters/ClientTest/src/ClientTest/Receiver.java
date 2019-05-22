package ClientTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Receiver implements Runnable {

    private volatile boolean started;
    private long timeStart;
    private AtomicInteger flag;
    private int num;
    private long[] time;
    private long timeFinish;
    private volatile boolean askToStart;
    private String selectedFilter;
    private BufferedImage image;
    private static final int serverPort = 10001;
    private static final String serverIP = "127.0.0.1";
    private DataInputStream in;
    private volatile boolean active;
    private DataOutputStream out;

    public Receiver(long[] time, int num, AtomicInteger flag) {
        active = true;
        this.flag = flag;
        this.time = time;
        this.num = num;
        askToStart = false;
        started = false;
        try {
            InetAddress ipAddress = InetAddress.getByName(serverIP);
            Socket socket = new Socket(ipAddress, serverPort);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("Server is not available!!");
            System.exit(0);
        }
    }

    public void setSelectedFilter(String selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

    public void loadImage(File imageFile) {
        try {
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<String> getFiltersList() {
        LinkedList<String> list = new LinkedList<>();
        try {
            int size = Integer.parseInt(in.readUTF());
            sendMSG("Ready");
            String[] filters = in.readUTF().split("\\|");
            for (int i = 0; i < size; i++) {
                list.add(filters[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void sendStartMSG(String selectedFilter, BufferedImage image) {
        try {
            sendMSG("START");
            started = true;
            System.out.println("started");
            in.readUTF();
            sendMSG(selectedFilter);
            in.readUTF();
            sendImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMSG(String MSG) throws IOException {
        out.writeUTF(MSG);
        out.flush();
    }

    private void sendImage(BufferedImage image) {
        try {
            //System.out.println("send start");
            int height = image.getHeight();
            int width = image.getWidth();
            sendMSG(Integer.toString(image.getWidth()));
            in.readUTF();
            sendMSG(Integer.toString(image.getHeight()));
            in.readUTF();
            sendMSG(Integer.toString(image.getType()));
            in.readUTF();
            String s;
            for (int i = 0; i < width; i++) {
                s = "";
                for (int j = 0; j < height; j++) {
                    s += Integer.toString(image.getRGB(i, j)) + "|";
                }
                sendMSG(s);
                in.readUTF();
            }
            //System.out.println("send finish");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setAskToStart(boolean askToStart) {
        this.askToStart = askToStart;
    }

    private BufferedImage receiveImage() {
        BufferedImage image = null;
        try {
            int width = Integer.parseInt(in.readUTF());
            sendMSG("Ready!");
            int height = Integer.parseInt(in.readUTF());
            sendMSG("Ready!");
            int type = Integer.parseInt(in.readUTF());
            sendMSG("Ready!");
            image = new BufferedImage(width, height, type);
            //System.out.println("receive start");
            for (int i = 0; i < width; i++) {
                String[] pixels = in.readUTF().split("\\|");
                for (int j = 0; j < height; j++) {
                    image.setRGB(i, j, Integer.parseInt(pixels[j]));
                }
                sendMSG("Ready");
            }
           // System.out.println("receive finished");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private void finish() {
        started = false;
        active = false;

    }

    public void run() {
        String line;
        while (active) {
            try {
                if (askToStart) {
                    timeStart = System.currentTimeMillis();
                    sendStartMSG(selectedFilter, image);
                    askToStart = false;
                }
                if (started) {
                    line = in.readUTF();
                    switch (line) {
                        case "PROGRESS":
                            sendMSG("Ready!");
                            in.readUTF();
                            sendMSG("Ready!");
                            break;
                        case "FINISHED":
                            sendMSG("Ready!");
                            BufferedImage image = receiveImage();
                            if (image == null) {
                                continue;
                            }
                            timeFinish = System.currentTimeMillis();
                            sendMSG("DISCONNECT");
                            time[num] = timeFinish - timeStart;
                            ImageIO.write(image, "jpg", new File("result.jpg"));
                            finish();
                            flag.getAndDecrement();
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}
