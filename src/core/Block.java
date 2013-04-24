package core;

public class Block {

    public static final double minDist;
    public double x;
    public double y;

    public String color;
    public long time;
    
    public Block(double x, double y, String color) {
        this.x = x;
        this.y = y;
        this.color= color;
    }
    
    

    public void setPosition(double botY, double botY, double botTheta) {
        r =Math.sqrt( Math.pow(this.relX,2) + Math.pow(this.relY,2));

        phi = Math.atan(this.relX/this.relY);

        x = botX + r * Math.cos(phi + botTheta);
        y = botY + r * Math.sin(phi + botTheta);
    }

    public Boolean isOnMap(ArrayList<Block> BlocksOnMap){
        for (Block b : BlocksOnMap){
            if(this.distanceToBlock(b) < minDist && b.color.equals(this.color)){
                return True;
            }
        }
        return False;
    }

    public double distanceToBlock(Block otherBlock){
        deltaX = this.x - otherBlock.x;
        deltaY = this.y - otherBlock.y;
        return Math.sqrt(Math.pow(deltaX,2) + Math.pow(deltaY,2));
    }

    public double distanceTo(double x, double y){
     
        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
    }

}
