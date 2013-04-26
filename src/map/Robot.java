package map;

public class Robot extends Polygon {
    public Point center;
    public double theta;

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

        x = center.x + r * Math.cos(phi + theta);
        y = center.y + r * Math.sin(phi + theta);

        return new Point(x, y);
    }
}
