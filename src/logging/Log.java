package logging;

import map.Map;

public class Log {
    private static Log instance;
    
    private RobotGraph graph;
    
    private Log() {
        graph = new RobotGraph(Map.getInstance());
    }
    
    private Log(RobotGraph graph) {
        this.graph = graph;
    }
    
    public static Log getInstance() {
        if (instance == null)
            instance = new Log();
        return instance;   
    }
    
    public static Log getInstance(RobotGraph graph) {
        if (instance == null)
            instance = new Log(graph);
        return instance;   
    }

    public void updatePose() {
        graph.repaint();
    }
    
    public static void log(String s) {
        System.out.println(s);
    }
}
