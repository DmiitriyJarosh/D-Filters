package sample;

import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

public class Client {


    private static final String defaultImageFilename = "default.jpg";

    private ImageView imageView;
    private Controller controller;
    private BufferedImage bufferedImage;
    private List<String> filterList;
    private String selectedFilter;
    private Receiver receiver;

    public Client() {
        receiver = new Receiver(this);
        new Thread(receiver).start();
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
        List<String> list = getFilterList();
        ObservableList<String> filters = controller.getComboBoxFilter().getItems();
        filters.addAll(list);
        controller.getComboBoxFilter().setItems(filters);
        setImageView();
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    private void setImageView() {
        this.imageView = controller.getImageView();
        try {
            Image image = SwingFXUtils.toFXImage(ImageIO.read(new File(defaultImageFilename)), null);
            imageView.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSelectedFilter(String selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

    public void showImage() {
        if (imageView != null) {
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(image);
        }
    }

    public void startWork(ProgressUpdater progressUpdater) {
        receiver.setProgressUpdater(progressUpdater);
        receiver.setSelectedFilter(selectedFilter);
        receiver.setImage(bufferedImage);
        receiver.setAskToStart(true);
        synchronized (receiver) {
            receiver.notify();
        }
    }
    public void disconnect() {
        if (receiver.isStarted()) {
            receiver.setFinishFlag(true);
            synchronized (this) {
                try {
                    receiver.setAskToBreak(true);
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        receiver.disconnect();
    }

    public void breakWork() {
        receiver.setAskToBreak(true);
    }

    public List<String> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<String> filterList) {
        this.filterList = filterList;
    }

    public void loadImage(File image) {
        try {
            bufferedImage = ImageIO.read(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
