package core;

import core.Config.Color;

public class Block {

    public double x;
    public double y;
    
    public double relX;
    public double relY; 
    public int sizeP; 
    public Color color;
    
    public Block(double relX, double relY, int sizeP, Color color){
    	this.relX = relX; 
    	this.relY = relY; 
    	this.sizeP = sizeP; 
    	this.color = color; 
    }

    // setPosition Transforms to coordinate positions of the world. 
    // Taking into account the angle of the robot to the world. 
    public void setPosition(double botX, double botY, double botTheta) {
        double r =Math.sqrt( Math.pow(this.relX,2) + Math.pow(this.relY,2));

        double phi = Math.atan(this.relX/this.relY);

        x = botX + r * Math.cos(phi + botTheta);
        y = botY + r * Math.sin(phi + botTheta);
    }

}
