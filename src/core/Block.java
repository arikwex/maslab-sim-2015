package core;


import map.Point;
import core.Config.Color;

public class Block extends Point{

	public double relX;
	public double relY;
    public double x;
    public double y;
    public int sizeP; 
    public Color color;
    
    
    public Block(double x, double y,int sizeP, Color color) {
        super(x,y);
        this.color= color;
        this.sizeP = sizeP;
    }
    
    
 // setPosition Transforms to coordinate positions of the world. 
// Taking into account the angle of the robot to the world. 

    public void setPosition(double botX, double botY, double botTheta) {
        double r =Math.sqrt( Math.pow(this.relX,2) + Math.pow(this.relY,2));

        double phi = Math.atan(this.relX/this.relY);

        x = botX + r * Math.cos(phi + botTheta);
        y = botY + r * Math.sin(phi + botTheta);
    }
    
    public Color getColor(){
    	return this.color;
    }
}
