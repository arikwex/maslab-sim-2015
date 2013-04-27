package core;

public class Config {
    public static final double minDist = 0;

    public enum BlockColor {
        RED, GREEN, BLUE, YELLOW, NONE
    }

    public static double[][] sonarPositions = { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 },
            { 0, 0, 0 }, { 0, 0, 0 } };

    public static final double WHEELBASE = .4;
    public static final double TICKS_PER_REV = 65500;
    public static final double WHEEL_RADIUS = .0625;
    public static final double METERS_PER_TICK = WHEEL_RADIUS * Math.PI * 2 / TICKS_PER_REV;

    public static double[][] botPoly = new double[][] {{ -.2, -.3 }, { -.2, .1 }, { .2, .1 }, { .2, -.3 }};
}
