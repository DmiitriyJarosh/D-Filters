package sample;

import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class Controller {

    public Controller() {
        client = Main.client;
        Main.controller = this;
    }

    private Client client;

    @FXML
    private Button buttonExit;

    @FXML
    private Button buttonStart;

    @FXML
    private ProgressBar progressBar;

    private static final FileChooser fileChooser = new FileChooser();

    private volatile boolean started = false;

    @FXML
    private Button buttonLoad;

    @FXML
    private Button buttonBreak;

    @FXML
    private ImageView imageView;

    @FXML
    private ComboBox<String> comboBoxFilter;

    @FXML
    private void pressExit(ActionEvent event) {
        client.disconnect();
        System.exit(0);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Button getButtonStart() {
        return buttonStart;
    }

    public Button getButtonBreak() {
        return buttonBreak;
    }

    @FXML
    public void choice(ActionEvent event) {
        client.setSelectedFilter(comboBoxFilter.getSelectionModel().getSelectedItem());
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    @FXML
    private void pressStart(ActionEvent event) {
        if (!started) {
            progressBar.setProgress(0);
            started = true;
            buttonStart.setDisable(true);
            buttonBreak.setDisable(false);
            ProgressUpdater progressUpdater = new ProgressUpdater(progressBar);
            client.startWork(progressUpdater);
        }
    }

    @FXML
    public ComboBox<String> getComboBoxFilter() {
        return comboBoxFilter;
    }

    @FXML
    private void pressLoad(ActionEvent event) {
        File file = fileChooser.showOpenDialog(Main.primaryStage);
        if (file != null) {
            client.loadImage(file);
            if (client.getBufferedImage() != null) {
                Image image = SwingFXUtils.toFXImage(client.getBufferedImage(), null);
                imageView.setImage(image);
                buttonStart.setDisable(false);
            } else {
                buttonStart.setDisable(true);
            }
        } else {
            buttonStart.setDisable(true);
        }

    }

    @FXML
    private void pressBreak(ActionEvent event) {
        if (started) {
            client.breakWork();
            started = false;
            buttonStart.setDisable(false);
            buttonBreak.setDisable(true);
        }
    }
}
