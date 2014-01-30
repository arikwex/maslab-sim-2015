package vision;

import java.util.ArrayList;

public interface VisionInterface {
	
	public void snapshot();				// Applies a snapshot and analyzes the image
	
    public ArrayList<Ball> getBalls();	// Returns a list of balls (r,theta,color)
    
    public ArrayList<Wall>	getWalls();	// Returns a list of walls (endpoints, type, normal) 
}
