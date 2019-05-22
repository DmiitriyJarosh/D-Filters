package sample;

import javafx.scene.control.ProgressBar;

public class ProgressUpdater {

    private ProgressBar progressBar;

    public ProgressUpdater(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void setProgress(double progress) {
       progressBar.setProgress(progress);
    }
}
