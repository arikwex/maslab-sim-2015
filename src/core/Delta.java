package core;

import java.util.ArrayList;

import orc.Orc;

import uORCInterface.OrcController;

import map.Point;
import firmware_interfaces.DeltaInterface;

public class Delta {
    private static Delta instance;

    private DeltaInterface di;
    private OrcController orc;

    private int[] position = new int[]{0,0,0};
    private ArrayList<double[]> moves;

    private int heldBlock = 0;
    private boolean pneumaticOut = false;
    
    long moveEndTime = 0;

    public Point[] DELTA_POSITION;

    private Delta() {
        di = new DeltaInterface();
        //orc = new OrcController(new int[]{0,1});

        double side = Config.DELTA_SIDE;
        DELTA_POSITION = new Point[3];
        DELTA_POSITION[0] = new Point(-side / 2, -side / (2 * Math.sqrt(3)));
        DELTA_POSITION[1] = new Point(0, side / Math.sqrt(3));
        DELTA_POSITION[2] = new Point(side / 2, -side / (2 * Math.sqrt(3)));
        
        moves = new ArrayList<double[]>();

        System.out.println("Pos 1: " + DELTA_POSITION[0] + " Pos 2: " + DELTA_POSITION[1] + " Pos 3: "
                + DELTA_POSITION[2]);
    }

    public static Delta getInstance() {
        if (instance == null)
            instance = new Delta();
        return instance;
    }
    
    public void setPneumatic(boolean extend) {
        di.setPneumatic(extend);
    }

    public void queueMove(double[] move) {
        moves.add(move);
    }

    public void step() {
        double[] move;
        long now = System.currentTimeMillis();
        
        if (!moves.isEmpty() && di.ready && now > moveEndTime) {
            move = moves.remove(0);
            
            if (move.length == 2) {
                moveEndTime = now + (int)(move[0]*1000);
                setPneumatic(move[1] > 0);
            } else if (move.length == 3) {
                move(move);
            }
        }
    }

    public void move(double[] move) {
        double z = move[2];
        Point pos = new Point(move[0], move[1]);

        int[] steps = { 0, 0, 0 };

        double dist, absCm;
        int absStep, relStep;
        for (int i = 0; i < position.length; i++) {
            dist = pos.distance(DELTA_POSITION[i]);
            absCm = Math.sqrt(Math.pow(Config.DELTA_LINK_LENGTH, 2) - Math.pow(dist, 2)) + Config.DELTA_ZERO_OFFSET + z;
            absStep = (int) (absCm * Config.DELTA_MICROSTEPS_PER_CM);
            relStep = absStep - position[i];
            
            steps[i] = relStep;
            
            System.out.println("AbsCM: " + absCm + " absStep: " + absStep + " relStep: " + relStep);
            
            position[i] = absStep;
            if (position[i] > 0)
                position[i] = 0;
        }
        
        di.move(steps);
    }

    public void performSequence(double[][] moves) {
        for (int i = 0; i<moves.length; i++)
            queueMove(moves[i]);
    }
    
    public static void main(String[] args) throws Exception {

        //OrcController orc = new OrcController(new int[]{0,1});
        //orc.digitalSet(3, true);
        
        //System.out.println("pin set");
        //Thread.sleep(10000000);
        
        Delta delta = new Delta();
        System.out.println("Started");
        
        Thread.sleep(1000);
        
        //double[][] sequence = new double[][] {Config.DELTA_TOP_OUT, {-1, 8, 20}, {-1, 8, 0}, {-1, 8, 40}, {10, 20, 40}};
        //delta.move(new double[]{9.5, 15, 25.6});
        //delta.move(new double[]{20, -5, 20});
        
        
        //double[][] sequence = new double[][] {Config.DELTA_TOP_OUT, {3,1}, {3,-1}, Config.DELTA_TOP_OUT};
        //delta.performSequence(sequence);
        
        
        // pick up block from left
        double[][] sequence = new double[][] {Config.DELTA_TOP_OUT, {20, 0, 20}, {19, -4, 20}, {18.7, -4, 13.5}, {18.7, -3.2, 13.5}, {18.7, -3.2, 12.8}, {2,1}, {18.7, -3.2, 25}, {20, 0, 25}, Config.DELTA_TOP_OUT};
        delta.performSequence(sequence);
        
        
        // pick up single block from ground
        //double[][] sequence = new double[][] {Config.DELTA_TOP_OUT, {-1, 8, 20}, {-1, 8, -1.2}, {4,0}, {-1, 8, 20}, Config.DELTA_TOP_OUT};
        //delta.performSequence(sequence);
        
        // deliver block to left container
        //sequence = new double[][] {Config.DELTA_TOP_OUT, {9.5, 15, 25.6}, {11.5, 16, 26.3}, {11.5, 16, 23}, {11.5, 16, 26.3}, {9.5, 15, 25.6}, Config.DELTA_TOP_OUT};
        //delta.performSequence(sequence);
        
        
        while(true) {
            delta.step();
            Thread.sleep(500);
        }
    }
}