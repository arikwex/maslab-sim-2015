package logging;

import javax.swing.SwingUtilities;

public class Log {
    private static Log instance;
    
    private RobotGraph graph;
    
    private Log() {
        graph = new RobotGraph();
    }
    
    public static Log getInstance() {
        if (instance == null)
            instance = new Log();
        return instance;   
    }

    public void updatePose() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                graph.repaint();
            }
        });
    }
    
    public synchronized static void log(String s) {
//    	System.out.println(s);
    }
}
