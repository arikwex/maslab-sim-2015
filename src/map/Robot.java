package map;

public class Robot extends Polygon {
    public Pose pose;

    public Robot() {
        super();
    }

    public void scale(double ratio) {

    }

    public void rotate(double theta) {

    }

    public Point getAbsolute(double x, double y) {
        double r = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

        double phi = Math.atan(x / y);

        x = pose.x + r * Math.cos(phi + pose.theta);
        y = pose.y + r * Math.sin(phi + pose.theta);

        return new Point(x, y);
    }
}
