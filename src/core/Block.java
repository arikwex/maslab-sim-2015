package core;


import java.util.ArrayList;

import map.Point;
import map.Pose;
import core.Config.BlockColor;

public class Block extends Point{

	public double i;
	public double j;
    public double x;
    public double y;
    public int sizeP; 
    public BlockColor color;
	public double relX;
	public double relY;

    /*
    public Block(double x, double y,int sizeP, BlockColor color) {
        super(x,y);
        this.color= color;
        this.sizeP = sizeP;
    }
    */
    
    public Block(double i, double j,int sizeP, BlockColor color) {
    	this.i= i;
    	this.j = j;
        this.color= color;
        this.sizeP = sizeP;
    }
    
 // setPosition Transforms to coordinate positions of the world. 
// Taking into account the angle of the robot to the world. 

    public void setPosition(double botX, double botY, double botTheta) {
        double r =Math.sqrt( Math.pow(this.i,2) + Math.pow(this.j,2));

        double phi = Math.atan(this.i/this.j);

        x = botX + r * Math.cos(phi + botTheta);
        y = botY + r * Math.sin(phi + botTheta);
    }
    
    public void setPosition(Pose bot) {
    	setPosition(bot.x,bot.y,bot.theta);
    }
    
	//compute distance of robot from current 
	public void updateRelXY(double width) {
			double targetRange = (0.05*2 /Math.sqrt((sizeP))*(width/Config.fieldOfViewHoriz)); 
			double targetBearing = (i-(width/2))*(Config.fieldOfViewHoriz/width);

			System.out.println("CamMin = "+Config.CAMYMINDIST+", CamMax "+Config.CAMYMAXDIST);
			
			relY = (Config.PIXELHEIGHT-j) * ((Config.CAMYMAXDIST-Config.CAMYMINDIST)/Config.PIXELHEIGHT);
			relX = (i-(Config.PIXELWIDTH/2)) * ((Config.CAMXDIST)/Config.PIXELWIDTH);
			System.out.println("rely = "+relY+", relx = "+relX);
			//relY = ((targetRange*Math.cos(targetBearing)-2)*10);
			//relX = targetRange*Math.sin(targetBearing);
	}
	
    public BlockColor getColor(){
    	return this.color;
    }
}
