package map;

import java.awt.Color;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.text.*;

import map.geom.Obstacle;

public class PolygonMap {

    protected Point2D.Double robotStart = new Point2D.Double();
    protected Point2D.Double robotGoal = new Point2D.Double();
    protected Rectangle2D.Double worldRect = new Rectangle2D.Double();
    protected LinkedList<Obstacle> obstacles = new LinkedList<Obstacle>();

    private String mapFile = "/home/rss-staff/ros/rss/solutions/lab6/src/global-nav-maze-2011-basic.map";
}
