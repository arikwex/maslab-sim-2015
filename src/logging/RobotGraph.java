package logging;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import map.Obstacle;
import map.Robot;

public class RobotGraph extends JFrame {
    private static final long serialVersionUID = -1299466487663318439L;

    private double[] pose = { 0, 0, 0 };
    private List<double[]> poseHistory = new ArrayList<double[]>();

    private PaintablePanel p;

    private static final int FRAME_HEIGHT = 500;
    private static final int FRAME_WIDTH = 500;
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

    public ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    public Robot bot;
    
    private boolean drawCSpace = true;

    private MyMouseListener ml = new MyMouseListener();

    private class MyMouseListener implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
        // TODO put in a thread to make this animation continuous
        private int[] start_drag = new int[2];

        public void mouseClicked(MouseEvent e) {
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
            int notches = e.getWheelRotation();
            if (notches < 0) { // zoom out, mag gets smaller
                total_mag /= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                x_max /= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                x_min /= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                y_max /= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
                y_min /= MAG_INCREMENT_PER_MOUSE_WHEEL_NOTCH;
            } else { // zoom in, mag gets bigger
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
        setPreferredSize(new Dimension(501, 532));

        setWidgets();
        poseHistory.add(new double[] { 0, 0, 0 });

        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

            // g.fillRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
            g.setColor(Color.black);

            // transform to robot world coordinates
            double xscale = FRAME_WIDTH / (x_max - x_min);
            double yscale = FRAME_HEIGHT / (y_max - y_min);
            AffineTransform t = new AffineTransform();
            t.scale(1.0, -1.0);
            t.translate(FRAME_WIDTH / 2 - ((x_min + x_max) / 2.0 * xscale), -FRAME_WIDTH / 2
                    - ((y_min + y_max) / 2.0 * yscale));
            t.scale(xscale, yscale);
            g.setTransform(t);

            // set the stroke so that when it zooms in all the lines still look
            // thin.
            g.setStroke(new BasicStroke((float) (1.0f * (x_max - x_min) / (FRAME_WIDTH * total_mag))));

            // TODO erase stuff here I suppose?

            // draw the grid
            drawGrid(g);
            drawAxes(g);

            paintObstacles(g);
            paintBot(g);
        }

        private void paintObstacles(Graphics2D g) {
            for (Obstacle o : obstacles) {
                g.setColor(o.color);
                g.draw(o.getPath());
                if (drawCSpace)
                    g.draw(o.getNaiveCSpace().getPath());
            }
        }
        
        private void paintBot(Graphics2D g) {
            AffineTransform t = new AffineTransform();
            t.setToIdentity();
            t.translate(bot.pose.x, bot.pose.y);
            t.rotate(bot.pose.theta);

            g.setColor(bot.color);
            g.draw(t.createTransformedShape(bot.getPath()));
        }

        private void eraseBot(Graphics2D g) {
            Path2D botPath = bot.getPath();
            g.setColor(new Color(238, 238, 238));
            double w = botPath.getBounds2D().getWidth() / 2 + 1;
            double h = botPath.getBounds2D().getHeight() / 2 + 1;
            Shape s = new Rectangle2D.Double(bot.pose.x - w, bot.pose.y - h, botPath.getBounds2D().getWidth() * 2,
                    botPath.getBounds2D().getHeight() * 2);
            g.fill(s);
        }

        /**
         * Draws the grid in green
         */
        private void drawGrid(Graphics2D g) {
            Color orig = g.getColor();
            g.setColor(Color.GREEN);

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

    public void addPolygon(Obstacle o) {
        obstacles.add(o);
    }

    public static void main(String[] args) {
        JFrame f = new RobotGraph();
        f.setPreferredSize(new Dimension(FRAME_WIDTH + 1, FRAME_HEIGHT + 32));
    }
}
