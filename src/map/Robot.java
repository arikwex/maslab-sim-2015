package map;

import java.awt.Color;

import core.Config;

public class Robot extends Polygon {
    public Pose pose;

    public Robot(double x, double y, double theta) {
        super();
        this.pose = new Pose(x, y, theta);
        this.color = Color.red;
        
        for (int i = 0; i<Config.botPoly.length; i++) {
            this.addVertex(new Point(Config.botPoly[i][0], Config.botPoly[i][1]));
        }
        this.close();
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
