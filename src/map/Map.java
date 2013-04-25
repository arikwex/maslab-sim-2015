package map;

import java.util.ArrayList;

import core.Block;
import core.Config.Color;
import core.Config;
public class Map {
    ArrayList<Obstacle> obstacles;
    ArrayList<MapBlock> blocks;
    //Robot bot;

    //takes bot +
    public Map(double margin) {

    }

    public boolean addBlock(Block b) {
        if(this.isOnMap(b)){
        	MapBlock tempBlock = new MapBlock(b);
        	blocks.add(tempBlock); 
        	return true;
        }
        else{
        	return false;
        }
    }

    public void moveRobot(double x, double y, double theta) {

    }

    public void checkSegment(Segment seg) {

    }

    public void nearestIntersectingSegment(Segment seg) {

    }
    
    public boolean isOnMap(Block block){
        for (MapBlock b : blocks){
        	Color color = b.getColor();
            if(b.distance(block) < Config.minDist && color == block.getColor() ){
                return true;
            }
        }
        return false;
    }

}
