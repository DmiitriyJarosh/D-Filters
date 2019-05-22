package ImageFilters;

import java.security.InvalidParameterException;


public class Filter {

    private final double[][] coefficients;
    private final int sizeX;
    private final int sizeY;

    public Filter(double[][] coefficients) {
        int sizeX = coefficients[0].length;
        for (double[] line : coefficients) {
            if (line.length != sizeX) {
                throw new InvalidParameterException("Coefficients array must be rectangle");
            }
        }
        this.coefficients = coefficients;
        this.sizeX = sizeX;
        this.sizeY = coefficients.length;
    }


    public int getSizeY() {
        return sizeY;
    }

    public int getSizeX() {
        return sizeX;
    }

    public double getCoefficient(int i, int j) {
        return coefficients[i][j];
    }

    public static final Filter GAUSSIAN_3X3 = new Filter(new double[][]{
            {1.0 / 16, 2.0 / 16, 1.0 / 16},
            {2.0 / 16, 4.0 / 16, 2.0 / 16},
            {1.0 / 16, 2.0 / 16, 1.0 / 16}
    });

    public static final Filter GAUSSIAN_5X5 = new Filter(new double[][]{
            {1.0 / 273, 4.0 / 273, 7.0 / 273, 4.0 / 273, 1.0 / 271},
            {4.0 / 273, 16.0 / 273, 26.0 / 273, 16.0 / 273, 4.0 / 273},
            {7.0 / 273, 26.0 / 273, 41.0 / 273, 26.0 / 273, 7.0 / 273},
            {4.0 / 273, 16.0 / 273, 26.0 / 273, 16.0 / 273, 4.0 / 273},
            {1.0 / 273, 4.0 / 273, 7.0 / 273, 4.0 / 273, 1.0 / 271}
    });

    public static final Filter SOBEL_X = new Filter(new double[][]{
            {-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}
    });

    public static final Filter SOBEL_Y = new Filter(new double[][]{
            {-1, -2, -1},
            {0, 0, 0},
            {1, 2, 1}
    });

    public static final Filter SCHARR_X = new Filter(new double[][]{
            {3, 0, -3},
            {10, 0, -10},
            {3, 0, -3}
    });

    public static final Filter SCHARR_Y = new Filter(new double[][]{
            {3, 10, 3},
            {0, 0, 0},
            {-3, -10, -3}
    });

}
