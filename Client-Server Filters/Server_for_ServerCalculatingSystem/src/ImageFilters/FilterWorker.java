package ImageFilters;
import Server.ImageModifier;

import java.awt.image.BufferedImage;

public class FilterWorker {

    private int width;
    private int height;
    private BufferedImage image;
    private BufferedImage newImage;
    private ImageModifier imageModifier;
    private boolean stopFlag;


    public FilterWorker(ImageModifier imageModifier, int width, int height, BufferedImage image, BufferedImage newImage) {
        this.image = image;
        stopFlag = false;
        this.imageModifier = imageModifier;
        this.height = height;
        this.newImage = newImage;
        this.width = width;
        initNewImage();
    }

    public void stop() {
        stopFlag = true;
    }

    public void setChannel(BufferedImage newImage, int x, int y, int c, int value) {
        int newValue = newImage.getRGB(x, y);
        switch (c) {
            case 0:
                newValue |= value;
                break;
            case 1:
                newValue |= (value << 8);
                break;
            case 2:
                newValue |= value << 16;
                break;
        }
        newImage.setRGB(x, y, newValue);
    }


    public int getChannel(int pixel, int c) {
        int value = 0;
        switch (c) {
            case 0:
                value = pixel & 0xFF;
                break;
            case 1:
                value = (pixel & 0xFF00) >> 8;
                break;
            case 2:
                value = (pixel & 0xFF0000) >> 16;
                break;
        }
        return value;
    }

    public void initNewImage() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newImage.setRGB(i, j, image.getRGB(i, j) & 0xFF000000);
            }
        }
    }


    public void applyAverage() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int sum = 0;
                for (int c = 0; c < 3; c++) {
                    sum += getChannel(image.getRGB(x, y), c);
                }
                for (int c = 0; c < 3; c++) {
                    setChannel(newImage, x, y, c, (sum / 3));
                }
                imageModifier.setProgress((double)(x * height + y) / (width * height));
                if (stopFlag) {
                    newImage = null;
                    return;
                }
            }
        }
    }


    public void applyFilter(Filter filter) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int c = 0; c < 3; c++) {
                    double sum = 0;
                    for (int i = x - filter.getSizeX() / 2; i <= x + filter.getSizeX() / 2; i++) {
                        for (int j = y - filter.getSizeY() / 2; j <= y + filter.getSizeY() / 2; j++) {
                            if (i >= 0 && i < width && j >= 0 && j < height) {
                                sum += (getChannel(image.getRGB(i, j), c)) * filter.getCoefficient(i - (x - filter.getSizeX() / 2), j - (y - filter.getSizeY() / 2));
                            }
                        }
                    }
                    setChannel(newImage, x, y, c, clump(sum));
                    imageModifier.setProgress((double)(x * height + y) / (width * height));
                    if (stopFlag) {
                        newImage = null;
                        return;
                    }
                }
            }
        }
    }


    public static int clump(double v) {
        if (v > 255)
            return 255;

        if (v < 0)
            return 0;

        return (int) v;
    }
}