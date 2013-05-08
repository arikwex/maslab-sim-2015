package utils;

public class Utils {
    public static double round(double v, int sigfig) {
        return Math.round(v * Math.pow(10, sigfig)) / Math.pow(10, sigfig);
    }

    public static double thetaDiff(double start, double end) {
        double thetaErr = end - start;
        return wrapAngle(thetaErr);
    }

    public static double wrapAngle(double angle) {
        if (angle > Math.PI)
            angle -= 2 * Math.PI;
        else if (angle < -Math.PI)
            angle += 2 * Math.PI;

        return angle;
    }
    

    public static double getMaxValue(double[] numbers) {
        double maxValue = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > maxValue) {
                maxValue = numbers[i];
            }
        }
        return maxValue;
    }

    public static double getMinValue(double[] numbers) {
        double minValue = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] < minValue) {
                minValue = numbers[i];
            }
        }
        return minValue;
    }
}
