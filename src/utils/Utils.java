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
}
