package Server;

import ImageFilters.Filter;
import ImageFilters.FilterWorker;
import java.awt.image.BufferedImage;

public class ImageModifier implements Runnable {

    private BufferedImage image;
    private String filter;
    private FilterWorker filterWorker;
    private double progress;

    public ImageModifier(BufferedImage image, String filter) {
        this.image = image;
        this.filter = filter;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public void stop() {
        if (filterWorker != null) {
            filterWorker.stop();
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public void run() {
        try {
            BufferedImage filteredImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            filterWorker = new FilterWorker(this, image.getWidth(), image.getHeight(), image, filteredImage);
            switch (filter) {
                case "monochrome":
                    filterWorker.applyAverage();
                    break;
                case "gaussian3x3":
                    filterWorker.applyFilter(Filter.GAUSSIAN_3X3);
                    break;
                case "gaussian5x5":
                    filterWorker.applyFilter(Filter.GAUSSIAN_5X5);
                    break;
                case "sobelX":
                    filterWorker.applyFilter(Filter.SOBEL_X);
                    break;
                case "sobelY":
                    filterWorker.applyFilter(Filter.SOBEL_Y);
                    break;
                case "scharrX":
                    filterWorker.applyFilter(Filter.SCHARR_X);
                    break;
                case "scharrY":
                    filterWorker.applyFilter(Filter.SCHARR_Y);
                    break;
            }
            image = filteredImage;
            if (image != null) {
                setProgress(1);
                System.out.println("Finished");
            } else {
                System.out.println("Filters stopped");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
