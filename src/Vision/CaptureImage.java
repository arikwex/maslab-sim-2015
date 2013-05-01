package Vision;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class CaptureImage extends Thread {

    public IplImage img,image;
    public int i;
	public boolean ready = false;
    private void captureFrame(OpenCVFrameGrabber grabber, CanvasFrame canvas) {
        // 0-default camera, 1 - next...so on
        try {
    		img = grabber.grab();
    		if (img != null)
    			canvas.showImage(img);
    		ready  = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	@Override
	public synchronized void run() {
		this.i = 1;
		final OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		grabber.setImageHeight(240);
		grabber.setImageWidth(320);
		CanvasFrame canvas = new CanvasFrame("canvas");
		try {grabber.start();		} catch (com.googlecode.javacv.FrameGrabber.Exception e) {}       
		while (true){
			i++;
    		captureFrame(grabber, canvas);
    	}
	}
	
	public static void main(String[] Args){
		CaptureImage me = new CaptureImage();
		me.run();
	}
}