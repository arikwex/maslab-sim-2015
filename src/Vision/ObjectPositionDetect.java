package Vision; 
//imports
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvInRangeS;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MEDIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetSpatialMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetCentralMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMoments;

import java.awt.Color;
import java.awt.Dimension;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;


import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;

import core.Block;

import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.CV_FILLED;
import static com.googlecode.javacv.cpp.opencv_core.CV_RGB;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvDrawContours;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_CCOMP;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvBoundingRect;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;

public class ObjectPositionDetect extends Thread {

	private int posX;
	private int posY;
	private double momX10;
	private double momY01;
	private double area;
	ArrayList<Block> blocks;
	ArrayList<Block> redBlocks;
	ArrayList<Block> blueBlocks;
	ArrayList<Block> greenBlocks;
	ArrayList<Block> yellowBlocks;
	CanvasFrame blocksCanvas = new CanvasFrame("Blocks Canvas");
    long lastTime;

	CanvasFrame canvas2 = new CanvasFrame("my image");
	private CvMemStorage mem;
	private CvSeq contours;
	private CvSeq ptr;
	private CvRect boundbox;
	private double round;
	private CvScalar sColor;
	private double a;
	private double b;
	private double c;
	private double num;
	private double den;
	private IplImage imgThreshold;
	private IplImage imgHSV;
	
    public void run() {
    	lastTime = System.currentTimeMillis();
    	CaptureImage capture = new CaptureImage();
    	capture.start();
    	try {Thread.sleep(2500);} catch (InterruptedException e) {}
    	while (true){
	    	if (capture.img != null){
		        IplImage orgImg = capture.img.clone();
		        lastTime = System.currentTimeMillis();
		        orgImg = getBlocks(Color.red, orgImg);
		        orgImg = getBlocks(Color.blue, orgImg);
		        orgImg = getBlocks(Color.green, orgImg);
		        orgImg = getBlocks(Color.yellow, orgImg);
		        System.out.println(System.currentTimeMillis()-lastTime);
		
		        canvas2.showImage(orgImg);
	        
	    	}
    	}
    }
private IplImage getBlocks(Color color, IplImage orgImg) {

    
    IplImage thresholdImage = hsvThreshold(orgImg, color);
	//blocksCanvas.showImage(thresholdImage);
    //cvSaveImage("hsvthreshold2.jpg", thresholdImage);
    //Dimension position = getCoordinates(thresholdImage, orgImg);
    //System.out.println("Dimension of original Image : " + thresholdImage.width() + " , " + thresholdImage.height());
    //System.out.println("Position of  spot    : x : " + position.width + " , y : " + position.height);

    contours = new CvSeq();

    ptr = new CvSeq();
    mem = cvCreateMemStorage(0);

    cvFindContours(thresholdImage, mem, contours, sizeof(CvContour.class) , CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
    
    if(contours.isNull())
    	   return orgImg;
    for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
    	
    	round = getRoundness(ptr);
        if (round < 0.45 || getArea(ptr)<225){
        	continue;
        }
        
        sColor = CV_RGB( color.getRed(), color.getGreen(), color.getBlue());
        cvDrawContours(orgImg, ptr, sColor, CV_RGB(0,0,0), -1, CV_FILLED, 8, cvPoint(0,0));
    	boundbox = cvBoundingRect(ptr, 0);
        cvRectangle( orgImg, cvPoint( boundbox.x(), boundbox.y() ), cvPoint( boundbox.x() + boundbox.width(), boundbox.y() + boundbox.height()),cvScalar( 0, 255, 255, 0 ), 3, 0, 0 ); 

        //System.out.println("Roundness of "+color.toString()+" spot    : " + round);
    }
    return orgImg;
}
	private double getArea(CvSeq contour) {
	    CvMoments moments = new CvMoments();
	    cvMoments(contour, moments, 1);
	    // cv Spatial moment : Mji=sumx,y(I(x,y)台j句i)
	    // where I(x,y) is the intensity of the pixel (x, y).
	    return cvGetCentralMoment(moments, 0, 0);
	
	}
	Dimension getCoordinates(IplImage thresholdImage, IplImage orgImg) {

        CvMoments moments = new CvMoments();
        cvMoments(thresholdImage, moments, 1);
        // cv Spatial moment : Mji=sumx,y(I(x,y)台j句i)
        // where I(x,y) is the intensity of the pixel (x, y).
        momX10 = cvGetSpatialMoment(moments, 1, 0); // (x,y)
        momY01 = cvGetSpatialMoment(moments, 0, 1);// (x,y)
        area = cvGetCentralMoment(moments, 0, 0);
        
        posX = (int) (momX10 / area);
        posY = (int) (momY01 / area);

        return new Dimension(posX, posY);
    }
	
	double getArea(IplImage image) {

        CvMoments moments = new CvMoments();
        cvMoments(image, moments, 1);
        // cv Spatial moment : Mji=sumx,y(I(x,y)台j句i)
        // where I(x,y) is the intensity of the pixel (x, y).
        area = cvGetCentralMoment(moments, 0, 0);
        return area;
	}
	
	double getRoundness(CvSeq ptr) {
        CvMoments moments = new CvMoments();
        cvMoments(ptr, moments, 1);
        // cv Spatial moment : Mji=sumx,y(I(x,y)台j句i)
        // where I(x,y) is the intensity of the pixel (x, y).
        a = cvGetCentralMoment(moments, 2, 0);
        b = cvGetCentralMoment(moments, 0, 2);
        c = cvGetCentralMoment(moments, 1, 1);
        num = a+b+Math.sqrt(Math.pow((a-b), 2)+4*Math.pow(c, 2));
        den = a+b-Math.sqrt(Math.pow((a-b), 2)+4*Math.pow(c, 2));
        return den/num;
	}

	IplImage hsvThreshold(IplImage orgImg, Color color) {
        // 8-bit, 3- color =(RGB)
    	imgHSV = cvCreateImage(cvGetSize(orgImg), 8, 3);
        //System.out.println(cvGetSize(orgImg));
        cvCvtColor(orgImg, imgHSV, CV_BGR2HSV);
        // 8-bit 1- color = monochrome
        imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);

        if (color.equals(Color.red)){
    		// cvScalar : ( H , S , V, A)
    		cvInRangeS(imgHSV, cvScalar(160, 125, 50, 0), cvScalar(180, 255, 255, 0), imgThreshold);
    	} else if (color.equals(Color.blue)){
            // cvScalar : ( H , S , V, A)
            cvInRangeS(imgHSV, cvScalar(110, 120, 20, 0), cvScalar(130, 255, 180, 0), imgThreshold);
        } else if (color.equals(Color.green)){
            // cvScalar : ( H , S , V, A)
            cvInRangeS(imgHSV, cvScalar(60, 50, 50, 0), cvScalar(100, 255, 255, 0), imgThreshold);
        } else if (color.equals(Color.yellow)){
            // cvScalar : ( H , S , V, A)
            cvInRangeS(imgHSV, cvScalar(10, 60, 100, 0), cvScalar(35, 255, 255, 0), imgThreshold);
        }
    	cvReleaseImage(imgHSV);
        cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 13);
        // save
        return imgThreshold;
    }
	
	public static void main(String[] Args){
		ObjectPositionDetect me = new ObjectPositionDetect();

		me.start();
	}
}