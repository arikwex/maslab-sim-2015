package core;

import core.Config.Color;

public class Block {

    
    public double x;
    public double y;
    
    public double relX;
    public double relY;
    
    public int sizeP;

    public Color color;
    public long time;
    
    public Block(double x, double y, Color color) {
        this.x = x;
        this.y = y;
        this.color= color;
    }
    
    

    public void setPosition(double botX, double botY, double botTheta) {
        double r =Math.sqrt( Math.pow(this.relX,2) + Math.pow(this.relY,2));

        double phi = Math.atan(this.relX/this.relY);

        x = botX + r * Math.cos(phi + botTheta);
        y = botY + r * Math.sin(phi + botTheta);
    }

    

    public double distanceToBlock(Block otherBlock){
        double deltaX = this.x - otherBlock.x;
        double deltaY = this.y - otherBlock.y;
        return Math.sqrt(Math.pow(deltaX,2) + Math.pow(deltaY,2));
    }

    public double distanceTo(double x, double y){
     
        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
    }

}
