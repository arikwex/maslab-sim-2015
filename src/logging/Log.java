package logging;

import map.Map;

public class Log {
    private static Log instance;
    
    private RobotGraph graph;
    
    private Log() {
        graph = new RobotGraph(Map.getInstance());
    }
    
    public static Log getInstance() {
        if (instance == null)
            instance = new Log();
        return instance;   
    }
    
    public void updatePose() {
        graph.repaint();
    }
    
    public static void log(String s) {
        System.out.println(s);
    }
}
