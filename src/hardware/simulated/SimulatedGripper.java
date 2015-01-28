package hardware.simulated;

import logging.Log;
import map.Map;
import map.elements.Stack;
import map.geom.Point;
import hardware.Hardware;
import hardware.enums.ElevatorState;
import hardware.enums.GripperState;

public class SimulatedGripper extends SimulatedServo {
	public SimulatedGripper(int pwm) {
		super(pwm);
	}
	
	public void setValue(double val) {
		desiredValue = val;
		estimateWorldInteraction();
	}
	
	public void estimateWorldInteraction() {
		// Simulate world interaction
		Map m = Map.getInstance();
		double elevation = Hardware.getInstance().servoElevation.getValue();
		double gripper = Hardware.getInstance().servoGrip.getValue();
		
		if (elevation != ElevatorState.TRANSIT.value) {
			Stack grabStack = null;
			for (int i = 0; i < m.getStacks().size(); i++) {
	    		Stack stack = m.getStacks().get(i);
	    		Point gripPoint = m.bot.getGripPoint();
	    		double dist = stack.pt.distance(gripPoint);
	    		if (dist < 0.05) {
	    			grabStack = stack;
	    			break;
	    		}
	    	}
			
			// Pick up a stack
			if (gripper == GripperState.CLOSE.value) {
				if (grabStack != null) {
					String cubes = grabStack.cubes;
					Point newLoc = new Point(grabStack.pt.x, grabStack.pt.y);
					
					if (elevation == ElevatorState.BOTTOM.value) {
						Log.log("Grabbing cubes [" + cubes + "] from stack [" + cubes + "]");
						m.bot.gripping = new Stack(m.bot.getGripPoint(), cubes);
						m.removeStack(grabStack);
					} else if (elevation == ElevatorState.MIDDLE.value) {
						Log.log("Grabbing cubes [" + cubes.substring(1) + "] from stack [" + cubes + "]");
						m.bot.gripping = new Stack(m.bot.getGripPoint(), cubes.substring(1));
						m.removeStack(grabStack);
						m.addStack(new Stack(newLoc, cubes.substring(0,1)));
					} else if (elevation == ElevatorState.TOP.value) {
						Log.log("Grabbing cube [" + cubes.substring(2) + "] from stack [" + cubes + "]");
						m.bot.gripping = new Stack(m.bot.getGripPoint(), cubes.substring(2));
						m.removeStack(grabStack);
						m.addStack(new Stack(newLoc, cubes.substring(0,2)));
					}
				}
			}
			
			// Drop off a stack
			if (gripper == GripperState.OPEN.value) {
				if (m.bot.gripping != null) {
					if (grabStack != null) {
						// Place stacks on top of existing stack
						String cubes = m.bot.gripping.cubes;
						Log.log("Dropping cubes [" + cubes + "] on top of existing stack [" + grabStack.cubes + "]");
						m.bot.gripping = null;
						grabStack.cubes += cubes;
					} else {
						// Place stacks on ground
						Log.log("Dropping cubes [" + m.bot.gripping.cubes + "] on ground");
						m.addStack(new Stack(m.bot.getGripPoint(), m.bot.gripping.cubes));
						m.bot.gripping = null;
					}
				}
			}
		}
	}
}
