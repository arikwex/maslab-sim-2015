package logging;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import map.Map;
import map.Pose;
import map.elements.HomeBase;
import map.geom.Obstacle;
import map.geom.Point;
import map.geom.Robot;
import map.geom.Segment;
import rrt.PathPlanning;
import state_machine.game.PlannerState;
import core.Config;
import core.Overlord;

public class RobotGraph extends JFrame implements Runnable {
    private static final long serialVersionUID = -1299466487663318439L;

    private List<double[]> poseHistory = new ArrayList<double[]>();

    private PaintablePanel p;

    private static final int FRAME_HEIGHT = 900;
    private static final int FRAME_WIDTH = 900;
    private static final double MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH = 1.05;
    private static final double X_MAX_INITIAL = 4.0;
    private static final double X_MIN_INITIAL = -4.0;
    private static final double Y_MAX_INITIAL = 4.0;
    private static final double Y_MIN_INITIAL = -4.0;
    
    private double total_mag = 1;

    // all values in meters
    private double x_step = 1.0;
    private double y_step = 1.0;
    private double x_max = X_MAX_INITIAL;
    private double x_min = X_MIN_INITIAL;
    private double y_max = Y_MAX_INITIAL;
    private double y_min = Y_MIN_INITIAL;

    public Robot bot;
    private boolean drawCSpace = true;
    public static Point pointer = null;

    private MyMouseListener ml = new MyMouseListener();

    private Map map;
    private PathPlanning pp;

    private class MyMouseListener implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
        private int[] start_drag = new int[2];

        public void mouseClicked(MouseEvent e) {
        	// TODO allow clicking to show mouse coord
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            start_drag[0] = e.getX();
            start_drag[1] = e.getY();
        }

        public void mouseDragged(MouseEvent e) {
            // we need to find the x and y translation
            int diff_x = e.getX() - start_drag[0];
            int diff_y = e.getY() - start_drag[1];
            double dx = -diff_x / (1.0 * FRAME_WIDTH) * (x_max - x_min);
            x_min += dx;
            x_max += dx;
            double dy = +diff_y / (1.0 * FRAME_HEIGHT) * (y_max - y_min);
            y_min += dy;
            y_max += dy;
            start_drag[0] = e.getX();
            start_drag[1] = e.getY();
            repaint();
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
            // TEST to zoom on center of screen.
            int notches = e.getWheelRotation() * 2;
            if (notches < 0) { // zoom out, mag gets smaller
                total_mag /= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                x_max /= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                x_min /= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                y_max /= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                y_min /= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
            } else if (notches > 0) { // zoom in, mag gets bigger
                total_mag *= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                x_max *= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                x_min *= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                y_max *= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                y_min *= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
            }
            repaint();
        }

        public void keyPressed(KeyEvent e) {
            if (e.isControlDown()) {
                switch (e.getKeyCode()) {
                case KeyEvent.VK_DOWN:
                    // zoom in, mag gets bigger
                    total_mag *= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                    x_max *= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                    x_min *= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                    y_max *= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                    y_min *= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                    break;
                case KeyEvent.VK_UP:
                    // zoom out, mag gets smaller
                    total_mag /= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                    x_max /= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                    x_min /= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                    y_max /= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                    y_min /= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                    break;
                }
            } else {
                double dx = 0;
                double dy = 0;
                switch (e.getKeyCode()) {
                case KeyEvent.VK_RIGHT:
                    dx = -.025 * (x_max - x_min);
                    break;
                case KeyEvent.VK_DOWN:
                    dy = .025 * (y_max - y_min);
                    break;
                case KeyEvent.VK_LEFT:
                    dx = .025 * (x_max - x_min);
                    break;
                case KeyEvent.VK_UP:
                    dy = -.025 * (y_max - y_min);
                    break;
                }
                x_max += dx;
                x_min += dx;
                y_max += dy;
                y_min += dy;
            }
            repaint();
        }

        public void keyReleased(KeyEvent e) {
        }

        public void keyTyped(KeyEvent e) {
        }
    }

    public RobotGraph() {
        // visually bring up the frame
        setPreferredSize(new Dimension(FRAME_WIDTH + 1, FRAME_HEIGHT + 32));

        map = Map.getInstance();
        bot = map.bot;
        pp = PathPlanning.getInstance();

        setWidgets();
        poseHistory.add(new double[] { 0, 0, 0 });
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

    }

    private void setWidgets() {
        p = new PaintablePanel();
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(p, BorderLayout.CENTER);
        this.setContentPane(contentPane);
        this.addMouseWheelListener(ml);
        this.addMouseListener(ml);
        this.addMouseMotionListener(ml);
        this.addKeyListener(ml);
    }

    public class PaintablePanel extends JPanel {
        private static final long serialVersionUID = 6617333561144522727L;

        public PaintablePanel() {
            super(true);
        }

        public void paint(Graphics g2) {
            Graphics2D g = (Graphics2D) g2;
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            g.setColor(Color.white);
            g.fillRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);

            // transform to robot world coordinates
            double xscale = FRAME_WIDTH / (x_max - x_min);
            double yscale = FRAME_HEIGHT / (y_max - y_min);
            AffineTransform t = new AffineTransform();
            t.scale(1.0, -1.0);
            t.translate(FRAME_WIDTH / 2 - ((x_min + x_max) / 2.0 * xscale), 
            		   -FRAME_WIDTH / 2 - ((y_min + y_max) / 2.0 * yscale));
            t.scale(xscale, yscale);
            g.setTransform(t);

            // set the stroke so that when it zooms in all the lines still look thin.
            BasicStroke regularLine = new BasicStroke((float) (1.0f * (x_max - x_min) / (FRAME_WIDTH * total_mag)));
            g.setStroke(regularLine);

            // draw the grid
            drawWorldRect(g);
            drawGrid(g);
            drawAxes(g);

            paintHomebase(g);
            paintLocations(g);
            paintRrt(g);
            paintPath(g);
            paintBot(g);
            paintObstacles(g);

            g.setColor(Color.CYAN);
            Segment seg = map.getBestApproach(bot.pose).trim(0.25);
            g.draw(new Line2D.Double(seg.start, seg.end));

            
            // mouse coords
            /*
            g.setTransform(new AffineTransform());
            g.setColor(Color.black);
            double[] rxfm = new double[6];
            t.getMatrix(rxfm);
            double mtx = (mousex - rxfm[4]);//rxfm[0];
            double mty = -(-FRAME_WIDTH / 2 - ((y_min + y_max) / 2.0 * yscale) + mousey)/yscale;
            g.drawString("MOUSE: " + (Math.round(mtx * 100)/100.0f) + ", " + (Math.round(mty * 100)/100.0f), 30, 60);            
            */
            g.setColor(Color.black);
            g.drawString("Time Remaining: " + (int)(Overlord.timeRemaining() / 1000.0) + "s", 30, 30);
        }
        
        private void paintLocations(Graphics2D g) {
        	List<Pose> locations = map.getLocations();
        	// skip starting location ---> size - platforms - homebase
        	for (int i = 1; i < locations.size() - map.getPlatforms().size() - 1; i++ ) {
        		Pose pose = locations.get(i);
        		paintPoint(g, pose, Color.MAGENTA);
        		Point[] hubs = PlannerState.getPorts(pose, pose.theta);
        		for (int j = 0; j < hubs.length; j++) {
        			g.draw(new Line2D.Double(pose, hubs[j]));
        		}
        	}
        }
        
        private void paintHomebase(Graphics2D g) {
        	Map m = Map.getInstance();
        	HomeBase hb = m.getHomeBase();
        	BasicStroke fatLine = new BasicStroke((float) (4.0f * (x_max - x_min) / (FRAME_WIDTH * total_mag)));
            Stroke oldStroke = g.getStroke();
            g.setStroke(fatLine);
            g.setColor(new Color(200,0,255,25));
        	g.fill(hb.getPath());
        	g.setColor(new Color(200,0,255,70));
            g.draw(hb.getPath());
            g.setStroke(oldStroke);
        }

        private void paintRrt(Graphics2D g) {
            PathPlanning pp = PathPlanning.getInstance();
            if (pp.rrtEdges != null && Control.getInstance().getMode() == ControlMode.TRAVEL_PLAN) {
	            g.setColor(new Color(0,0,255,128));
	            paintSegments(g, pp.rrtEdges);
            }
            if (pp.goal != null) {
            	paintPoint(g, pp.goal, Color.RED);
            }
        }
        
        private void paintPoints(Graphics2D g, Collection<Point> points) {
        	BasicStroke fatLine = new BasicStroke((float) (10.0f * (x_max - x_min) / (FRAME_WIDTH * total_mag)));
            Stroke oldStroke = g.getStroke();
            g.setStroke(fatLine);
            for (Point p : points)
            	g.draw(new Line2D.Double(p,p));
            
            g.setStroke(oldStroke);
        }
        
        private void paintSegments(Graphics2D g, Collection<Segment> segments) {
            for (Segment seg : segments)
                g.draw(new Line2D.Double(seg.start, seg.end));
        }

        private void paintObstacles(Graphics2D g) {
            if (map.getObstacles() == null)
                return;
            for (Obstacle o : map.getObstacles()) {
                o.paint(g);
            	g.setColor(new Color(0,0,0,128));
                if (drawCSpace) {
                    //g.draw(o.getMaxCSpace().getPath());
                    g.draw(o.getMinCSpace().getPath());
                    //g.draw(o.getPolyCSpace(bot.getRotated(bot.pose.theta)).getPath());
                }
            }
        }

        private void paintPoint(Graphics2D g, Point p, Color c) {
        	g.setColor(c);
        	double POINT_RADIUS = 0.03;
            double xMin = p.x - POINT_RADIUS;
            double yMin = p.y - POINT_RADIUS;
        	g.fill(new Ellipse2D.Double(xMin, yMin, POINT_RADIUS * 2, POINT_RADIUS * 2));
        }

        private synchronized void paintPath(Graphics2D g) {
            BasicStroke fatLine = new BasicStroke((float) (4.0f * (x_max - x_min) / (FRAME_WIDTH * total_mag)));
            Stroke oldStroke = g.getStroke();
            g.setStroke(fatLine);

            if (pointer != null) {
	            if (Control.getInstance().getMode() == ControlMode.TRAVEL_PLAN) {
	                if (pp.path == null || pp.path.size() < 1)
	                    return;
	                g.setColor(Color.RED);
		            Point start = bot.pose;
		            for (Point p : pp.path) {
		                g.draw(new Line2D.Double(start, p));
		                start = p;
		            }
		            g.draw(new Ellipse2D.Double(pointer.x-0.1, pointer.y-0.1, 0.2, 0.2));
	            } else if (Control.getInstance().getMode() == ControlMode.DRIVE_FORWARD) {
	            	g.setColor(Color.BLUE);
		            g.draw(new Line2D.Double(bot.pose, pointer));
		            g.draw(new Ellipse2D.Double(pointer.x-0.1, pointer.y-0.1, 0.2, 0.2));
	            } else if (Control.getInstance().getMode() == ControlMode.DRIVE_BACK) {
	            	g.setColor(Color.ORANGE);
		            g.draw(new Line2D.Double(bot.pose, pointer));
		            g.draw(new Ellipse2D.Double(pointer.x-0.1, pointer.y-0.1, 0.2, 0.2));
	            }
            }
            g.setStroke(oldStroke);
        }

        private void paintBot(Graphics2D g) {
            AffineTransform t = new AffineTransform();
            t.setToIdentity();
            t.translate(bot.pose.x, bot.pose.y);
            t.rotate(bot.pose.theta);

            g.setColor(new Color(255,180,50,200));
            g.fill(t.createTransformedShape(bot.getPath()));
            g.setColor(bot.color);
            g.draw(t.createTransformedShape(bot.getPath()));
            paintPoint(g, bot.pose, bot.color);

            paintPoint(g, new Point(0, 0), bot.color);
            
            // Draw held-objects
            Point gripPoint = bot.getGripPoint();
            if (bot.gripping != null) {
            	bot.gripping.paint(g);
            } else {
            	g.setColor(new Color(0,128,0,200));
            	g.draw(new Ellipse2D.Double(gripPoint.x-0.05, gripPoint.y-0.05, 0.1, 0.1));
            }
        }
        
        private void drawWorldRect(Graphics2D g) {
            AffineTransform t = new AffineTransform();
            t.setToIdentity();

            g.setColor(new Color(50,50,50,200));
            g.draw(t.createTransformedShape(map.getWorldRect()));
        }

        /**
         * Draws the grid in green
         */
        private void drawGrid(Graphics2D g) {
            Color orig = g.getColor();
            g.setColor(new Color(55,128,55,33));

            for (double i = x_min - 2 * x_step; i <= x_max + 2 * x_step; i += x_step) {
                g.drawLine((int) (Math.round(i)), (int) (Math.round(y_min - 2 * y_step)), (int) (Math.round(i)),
                        (int) (Math.round(y_max + 2 * y_step)));
            }
            for (double i = y_min - 2 * y_step; i <= y_max + 2 * y_step; i += y_step) {
                g.drawLine((int) (Math.round(x_min - 2 * x_step)), (int) (Math.round(i)),
                        (int) (Math.round(x_max + 2 * x_step)), (int) (Math.round(i)));
            }
            g.setColor(orig);
        }

        /**
         * Draws the axes in black
         */
        private void drawAxes(Graphics2D g) {
            Color orig = g.getColor();
            g.setColor(Color.BLACK);
            g.drawLine((int) (Math.round(x_min - 2 * x_step)), (int) (Math.round(0)),
                    (int) (Math.round(x_max + 2 * x_step)), (int) (Math.round(0)));
            g.drawLine((int) (Math.round(0)), (int) (Math.round(y_min - 2 * y_step)), (int) (Math.round(0)),
                    (int) (Math.round(y_max + 2 * y_step)));

            g.setColor(orig);
        }
    }

    public void run() {

        setPreferredSize(new Dimension(FRAME_WIDTH + 1, FRAME_HEIGHT + 32));
        while (true) {
            repaint();
        }
    }
}
