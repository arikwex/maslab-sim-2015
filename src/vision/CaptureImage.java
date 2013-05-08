package vision;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import core.Config;

public class CaptureImage {

    public IplImage img,image;
	final OpenCVFrameGrabber grabber;
	final CanvasFrame canvas;
	private void captureFrame(OpenCVFrameGrabber grabber, CanvasFrame canvas) {
        // 0-default camera, 1 - next...so on
        try {
    		img = grabber.grab();
    		//if (img != null)
    			//canvas.showImage(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public CaptureImage(){
    	grabber = new OpenCVFrameGrabber(0);
		grabber.setImageHeight(Config.PIXELHEIGHT);
		grabber.setImageWidth(Config.PIXELWIDTH);
		canvas = new CanvasFrame("raw image");
		try {grabber.start();		} catch (com.googlecode.javacv.FrameGrabber.Exception e) {}       

    }
    
	public synchronized void run() {
		while (true){
			step();
		}
	}
	
	public synchronized void step() {
		captureFrame(grabber, canvas);		
	}
	
	public static void main(String[] Args){
		CaptureImage me = new CaptureImage();
		me.run();
	}
}