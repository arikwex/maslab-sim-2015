package vision;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import vision.Wall.Type;

import logging.Log;

import Core.FilterOp;

import com.googlecode.javacv.VideoInputFrameGrabber;

// TODO: Replace this Vision singleton class with actual vision code
public class Vision implements VisionInterface {

    private static Vision instance;
    private VideoInputFrameGrabber grabber;
    FilterOp blurOp;
	FilterOp colorizeOp;
	FilterOp clipTopOp;
	private static BufferedImage processed;
	private final int DOWNSAMPLE = 1;
    
    public static void main ( String[] args ) {
    	TestBed tester = new TestBed(640,480);
    	
    	Vision v = Vision.getInstance();
    	//v.snapshot();
    	
    	
    	for ( int i = 0; i <= 100 ; i++ ) {
    		int img = i%7;
	    	try { 
	    		v.process( ImageIO.read( new File("camera\\maslab_"+img+".png") ) );
	    		tester.setImage(processed);
	    		Thread.sleep(1000);
	    	} catch ( Exception e ) {
	    		e.printStackTrace();
	    	}
    	}
    	
    	
    	try { 
    		v.process( ImageIO.read( new File("camera\\maslab_1.png") ) );
    		tester.setImage(processed);
    		Thread.sleep(1000);
    	} catch ( Exception e ) {
    		e.printStackTrace();
    	}
    }
    
    public static Vision getInstance() {
        if (instance == null) {
            instance = new Vision();
        }
        return instance;
    }
    
    public Vision() {
    	grabber = new VideoInputFrameGrabber(0);
    	grabber.setImageWidth(320);
    	grabber.setImageHeight(240);
		try { grabber.start(); } catch ( Exception e ) {}
		
		blurOp = new FilterOp("blur");
		colorizeOp = new FilterOp("colorize");
		clipTopOp = new FilterOp("clipTop");
		blurOp.setInt("kernel_size", 3);
    }
    
    @Override
    public void snapshot() {
    	try {
    		BufferedImage capture = grabber.grab().getBufferedImage();
    		process(capture);
    	} catch ( Exception e ) {
    		Log.log("[Vision] " + e.getLocalizedMessage());
    	}
    }
    
    public void process( BufferedImage bi ) {
    	// ShaderCL Computing
		blurOp.apply(bi);
		colorizeOp.apply();
		clipTopOp.apply();
		BufferedImage filtered = FilterOp.getImage();
		
		// Java Computing
		filtered = gameObjects( filtered );
		
		processed = filtered;
    }
    
    @Override
    public ArrayList<Ball> getBalls() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public ArrayList<Wall> getWalls() {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    public BufferedImage gameObjects( BufferedImage inp ) {
		BufferedImage res = new BufferedImage(400,400,BufferedImage.TYPE_INT_ARGB);
		Graphics g = res.getGraphics();
		List<Ball> balls = new ArrayList<Ball>();
		List<Wall> walls = new ArrayList<Wall>();
		Wall currWall = null;
		Vector2D prevSegment = null;
		
		int BALL_STRIDE = 4;
		int MAP_STRIDE = 10;
		
		// FoV
		double FoV = 120.0/57.3;
		double angle = FoV/2;
		g.setColor( Color.cyan );
		g.drawLine(200,200,(int)(200+150*Math.sin(angle)),(int)(200-150*Math.cos(angle)));
		g.drawLine(200,200,(int)(200+150*Math.sin(-angle)),(int)(200-150*Math.cos(-angle)));
		
		// Self
		double SCALE = 2;
		double ROBOT_DIAM = 13;
		g.setColor(new Color(0,255,0));
		g.drawRect(0,0,399,399);
		g.fillOval((int)(res.getWidth()/2.0-SCALE*ROBOT_DIAM/2.0),(int)(res.getHeight()/2.0-SCALE*ROBOT_DIAM/2.0),(int)(SCALE*ROBOT_DIAM),(int)(SCALE*ROBOT_DIAM));
		
		double Xprev = 0;
		double Zprev = 0;
		
		// Map walls
		for ( int x = 0; x < inp.getWidth(); x+=MAP_STRIDE ) {
			for ( int y = 0; y < inp.getHeight()-1; y++ ) {
				int pixel = inp.getRGB(x, y);
				if ( pixel==0xff00ffff ) {
					y = 10000;
					continue;
				}
				if ( pixel==0xffffffff ) {
					g.setColor(Color.white);
					double X = pixelToXspace(x,y);
					double Z = pixelToZspace(x,y);
					
					Vector2D currSegment = new Vector2D(X,Z);
					
					if ( prevSegment!=null ) {
						currWall = new Wall(prevSegment,currSegment,Type.Wall);
						Vector2D v = new Vector2D(prevSegment.x-currSegment.x, prevSegment.y-currSegment.y);
						if ( v.getMagnitude() < 10.0 ) {
							walls.add(currWall);
						}
					}
					
					prevSegment = currSegment;
					Xprev = X;
					Zprev = Z;
					y = 10000;
				}
			}
		}
		
		// Map balls
		for ( int x = 5; x < inp.getWidth()-5; x+=BALL_STRIDE ) {
			for ( int y = 0; y < inp.getHeight()*0.8; y+=BALL_STRIDE ) {
				int pixel = inp.getRGB(x, y);
				if ( pixel==0xff00ff00 || pixel==0xffff0000 ) {
					Point pt = findBottom(inp,x,y,pixel);
					
					if ( pt==null )
						continue;
					double X = pixelToXspace(pt.x,pt.y);
					double Z = pixelToZspace(pt.x,pt.y);
					
					// combine balls
					boolean merge = false;
					for ( int i = 0; i < balls.size(); i++ ) {
						Ball pb = balls.get(i);
						double dist = Math.sqrt((pb.x-X)*(pb.x-X)+(pb.z-Z)*(pb.z-Z));
						if ( dist < 3 ) {
							double nx = (X+pb.x)/2.0;
							double nz = (Z+pb.z)/2.0;
							pb.set(nx,nz);
							merge = true;
							break;
						}
					}
					if ( !merge ) {
						Ball np = new Ball(X,Z,true);
						if ( pixel==0xff00ff00 )
							np.isRed = false;
						balls.add(np);
					}
				}
			}
		}
		
		
		// DRAW WALLS
		for ( int i = 0; i < walls.size(); i++ ) {
			Wall wall = walls.get(i);
			g.setColor( new Color(255,255,255) );
			double X1 = -wall.left.x*SCALE+res.getWidth()/2;
			double Z1 = -wall.left.y*SCALE+res.getHeight()/2;
			double X2 = -wall.right.x*SCALE+res.getWidth()/2;
			double Z2 = -wall.right.y*SCALE+res.getHeight()/2;
			g.drawLine((int)X1,(int)Z1,(int)X2,(int)Z2);
		}
		
		// DRAW BALLS
		double DIAM = 3.75*SCALE;
		for ( int i = 0; i < balls.size(); i++ ) {
			Ball ball = balls.get(i);
			if ( ball.isRed)
				g.setColor( new Color(255,0,0) );
			else
				g.setColor( new Color(0,255,0) );
			double X = -ball.x*SCALE+res.getWidth()/2;
			double Z = -ball.z*SCALE+res.getHeight()/2;
			g.fillOval((int)(X-DIAM/2),(int)(Z-DIAM/2),(int)DIAM,(int)DIAM);
		}
		
		return res;
	}
	
	public Point findBottom( BufferedImage bi, int x, int y, int color ) {
		int dy = 0;
		while ( y<bi.getHeight()-2 ) {
			dy++;
			
			int mid = bi.getRGB(x, y+1);
			if ( mid==color ) {
				y++;
				continue;
			}
			
			int left = bi.getRGB(x-1, y+1);
			int right = bi.getRGB(x+1, y+1);
			if ( left==color ) {
				x--;
				y++;
			} else if ( right==color ) {
				x++;
				y++;
			}
			
			left = bi.getRGB(x-2, y+1);
			right = bi.getRGB(x+2, y+1);
			if ( left==color ) {
				x-=4;
				y++;
			} else if ( right==color ) {
				x+=4;
				y++;
			} else {
				break;
			}
		}
		
		if ( dy>14 )
			return new Point(x,y);
		else
			return null;
	}
	
	public double pixelToZspace( double x, double y ) {
		x*=DOWNSAMPLE;
		y*=DOWNSAMPLE;
		if ( x>=320 )
			x = 640-x;
		// modelZ = a + b*ArcTan[c + d*y] + e*x;
		
		return 249997. + 0.00160805*x - 159160 * Math.atan(424.882 + 28.1202*y);
	}
	
	public double pixelToXspace( double x, double y ) {
		x*=DOWNSAMPLE;
		y*=DOWNSAMPLE;
		
		double y2 = y*y;
		double y3 = y2*y;
		double y4 = y2*y2;
		double y5 = y3*y2;
		
		return 137.585 - 0.426639*x - 1.78582*y + 0.00553767*x*y + 0.0110176*y2 - 
		 0.0000341646*x*y2 - 0.0000357187*y3 + 1.1076*Math.pow(10,-7)*x*y3 + 
		 5.86206*Math.pow(10,-8)*y4 - 1.81777*Math.pow(10,-10)*x*y4 - 3.83795*Math.pow(10,-11)*y5 + 
		 1.19011*Math.pow(10,-13)*x*y5;
	}
}
