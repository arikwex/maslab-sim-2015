package core;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class CaptureImage {
    
    IplImage image;
    static CanvasFrame canvas = new CanvasFrame("Web Cam");
    
    public CaptureImage() {
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    }
    
    private static void captureFrame() {
        // 0-default camera, 1 - next...so on
        final OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        //final VideoInputFrameGrabber grabber = new VideoInputFrameGrabber(0);
        try {
            grabber.start();
            IplImage img = grabber.grab();
            if (img != null) {
                //cvSaveImage(name, img);
                //cvSaveImage("Image",img);
                canvas.showImage(img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws InterruptedException {
        System.out.println(System.getProperty("java.library.path"));
        System.loadLibrary("opencv_java245");

        while(true){
            captureFrame(); 
            Thread.sleep(100);
        }
    }     
}