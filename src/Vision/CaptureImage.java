package Vision;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

public class CaptureImage implements Runnable {

    static IplImage img;
	static IplImage image;
    private static void captureFrame() {
        // 0-default camera, 1 - next...so on
        final OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        try {
            grabber.start();
            img = grabber.grab();
            if (img != null) {
                cvSaveImage("capture.jpg", img);
                
                image = cvLoadImage("capture.jpg");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	@Override
	public void run() {
		while (true){
    		captureFrame();
    	}
	}
	
	public static void main(String[] Args){
		captureFrame();
	}
}