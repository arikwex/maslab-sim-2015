package logging;

import javax.swing.SwingUtilities;

import core.Config;

public class Log {
	private static Log instance;

	private RobotGraph graph;

	private Log() {
		if (Config.sim) {
			graph = new RobotGraph();
		}
	}

	public static Log getInstance() {
		if (instance == null)
			instance = new Log();
		return instance;
	}

	public void updatePose() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (Config.sim) {
					graph.repaint();
				}
			}
		});
	}

	public synchronized static void log(String s) {
		System.out.println(s);
	}
}
