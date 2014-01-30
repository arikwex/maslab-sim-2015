package vision;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

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
    
    public static void main ( String[] args ) {
    	TestBed tester = new TestBed(640,480);
    	
    	Vision v = Vision.getInstance();
    	//v.snapshot();
    	try { 
    		v.process( ImageIO.read( new File("camera/maslab_0.png") ) );
    	} catch ( Exception e ) {
    		e.printStackTrace();
    	}
    	
    	tester.setImage(processed);
    }
    
    public static Vision getInstance() {
        if (instance == null) {
            instance = new Vision();
        }
        return instance;
    }
    
    public Vision() {
    	//grabber = new VideoInputFrameGrabber(0);
    	//grabber.setImageWidth(320);
    	//grabber.setImageHeight(240);
		//try { grabber.start(); } catch ( Exception e ) {}
		
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
		//filtered = process( filtered );
		
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
}
