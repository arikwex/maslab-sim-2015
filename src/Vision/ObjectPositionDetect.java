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


import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;

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

public class ObjectPositionDetect {

    public static void main(String[] args) {
        IplImage orgImg = cvLoadImage("capture.jpg");

        CanvasFrame redcanvas = new CanvasFrame("red");
        IplImage redthresholdImage = hsvThreshold(orgImg, Color.red);
		redcanvas.showImage(redthresholdImage);
        cvSaveImage("hsvthreshold.jpg", redthresholdImage);
        Dimension redposition = getCoordinates(redthresholdImage, orgImg);
        System.out.println("Dimension of original Image : " + redthresholdImage.width() + " , " + redthresholdImage.height());
        System.out.println("Position of red spot    : x : " + redposition.width + " , y : " + redposition.height);

        CvMemStorage mem;
        CvSeq contours = new CvSeq();
        CvSeq ptr = new CvSeq();
        mem = cvCreateMemStorage(0);
        cvFindContours(redthresholdImage, mem, contours, sizeof(CvContour.class) , CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
        double round;
        CvRect boundbox;
        for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
        	round = getRoundness(ptr);
            if (round < 0.45 || getArea(ptr)<100){
            	continue;
            }
            CvScalar color = CV_RGB( Color.red.getRed(), Color.red.getGreen(), Color.red.getBlue());
            cvDrawContours(orgImg, ptr, color, CV_RGB(0,0,0), -1, CV_FILLED, 8, cvPoint(0,0));
        	boundbox = cvBoundingRect(ptr, 0);
            cvRectangle( orgImg, cvPoint( boundbox.x(), boundbox.y() ), cvPoint( boundbox.x() + boundbox.width(), boundbox.y() + boundbox.height()),cvScalar( 0, 255, 255, 0 ), 3, 0, 0 ); 

            System.out.println("Roundness of red spot    : " + round);
        }

        CanvasFrame bluecanvas = new CanvasFrame("blue");
        IplImage bluethresholdImage = hsvThreshold(orgImg, Color.blue);
		bluecanvas.showImage(bluethresholdImage);
        cvSaveImage("hsvthreshold.jpg", bluethresholdImage);
        Dimension blueposition = getCoordinates(bluethresholdImage, orgImg);
        System.out.println("Dimension of original Image : " + bluethresholdImage.width() + " , " + bluethresholdImage.height());
        System.out.println("Position of blue spot    : x : " + blueposition.width + " , y : " + blueposition.height);
        
        contours = new CvSeq();
        ptr = new CvSeq();
        mem = cvCreateMemStorage(0);
        cvFindContours(bluethresholdImage, mem, contours, sizeof(CvContour.class) , CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));

        for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
        	round = getRoundness(ptr);
            if (round < 0.45 || getArea(ptr)<100){
            	continue;
            }
            CvScalar color = CV_RGB( Color.blue.getRed(), Color.blue.getGreen(), Color.blue.getBlue());
            cvDrawContours(orgImg, ptr, color, CV_RGB(0,0,0), -1, CV_FILLED, 8, cvPoint(0,0));
        	boundbox = cvBoundingRect(ptr, 0);
            cvRectangle( orgImg, cvPoint( boundbox.x(), boundbox.y() ), cvPoint( boundbox.x() + boundbox.width(), boundbox.y() + boundbox.height()),cvScalar( 0, 255, 255, 0 ), 3, 0, 0 ); 

            System.out.println("Round of blue spot    : " + round);
        }
        
        CanvasFrame greencanvas = new CanvasFrame("green");
        IplImage greenthresholdImage = hsvThreshold(orgImg, Color.green);
		greencanvas.showImage(greenthresholdImage);
        cvSaveImage("hsvthreshold.jpg", greenthresholdImage);
        Dimension greenposition = getCoordinates(greenthresholdImage, orgImg);
        System.out.println("Dimension of original Image : " + greenthresholdImage.width() + " , " + greenthresholdImage.height());
        System.out.println("Position of green spot    : x : " + greenposition.width + " , y : " + greenposition.height);

        contours = new CvSeq();
        ptr = new CvSeq();
        mem = cvCreateMemStorage(0);
        cvFindContours(greenthresholdImage, mem, contours, sizeof(CvContour.class) , CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
        
        for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
        	round = getRoundness(ptr);
            if (round < 0.45 || getArea(ptr)<100){
            	continue;
            }
        	CvScalar color = CV_RGB( Color.green.getRed(), Color.green.getGreen(), Color.green.getBlue());
            cvDrawContours(orgImg, ptr, color, CV_RGB(0,0,0), -1, CV_FILLED, 8, cvPoint(0,0));
        	boundbox = cvBoundingRect(ptr, 0);
            cvRectangle( orgImg, cvPoint( boundbox.x(), boundbox.y() ), cvPoint( boundbox.x() + boundbox.width(), boundbox.y() + boundbox.height()),cvScalar( 0, 255, 255, 0 ), 3, 0, 0 ); 

            System.out.println("Roundness of green spot    : " + round);
        }

        
        CanvasFrame yellowcanvas = new CanvasFrame("yellow");
        IplImage yellowthresholdImage = hsvThreshold(orgImg, Color.yellow);
		yellowcanvas.showImage(yellowthresholdImage);
        cvSaveImage("hsvthreshold.jpg", yellowthresholdImage);
        Dimension yellowposition = getCoordinates(yellowthresholdImage, orgImg);
        System.out.println("Dimension of original Image : " + yellowthresholdImage.width() + " , " + yellowthresholdImage.height());
        System.out.println("Position of yellow spot    : x : " + yellowposition.width + " , y : " + yellowposition.height);

        contours = new CvSeq();
        ptr = new CvSeq();
        mem = cvCreateMemStorage(0);
        cvFindContours(yellowthresholdImage, mem, contours, sizeof(CvContour.class) , CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
        for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
        	round = getRoundness(ptr);
            if (round < 0.45 || getArea(ptr)<100){
            	continue;
            }CvScalar color = CV_RGB( Color.yellow.getRed(), Color.yellow.getGreen(), Color.yellow.getBlue());
            cvDrawContours(orgImg, ptr, color, CV_RGB(0,0,0), -1, CV_FILLED, 8, cvPoint(0,0));
            boundbox = cvBoundingRect(ptr, 0);
            cvRectangle( orgImg, cvPoint( boundbox.x(), boundbox.y() ), cvPoint( boundbox.x() + boundbox.width(), boundbox.y() + boundbox.height()),cvScalar( 0, 255, 255, 0 ), 3, 0, 0 ); 
        	
            System.out.println("Roundness of green spot    : " + round);
        }

        cvSaveImage("target.jpg", orgImg);
        CanvasFrame canvas = new CanvasFrame("image");
        canvas.showImage(orgImg);
    }
private static double getArea(CvSeq contour) {
    CvMoments moments = new CvMoments();
    cvMoments(contour, moments, 1);
    // cv Spatial moment : Mji=sumx,y(I(x,y)台j句i)
    // where I(x,y) is the intensity of the pixel (x, y).
    double area = cvGetCentralMoment(moments, 0, 0);
    return area;

	}
	static Dimension getCoordinates(IplImage thresholdImage, IplImage orgImg) {
        int posX = 0;
        int posY = 0;
        CvMoments moments = new CvMoments();
        cvMoments(thresholdImage, moments, 1);
        // cv Spatial moment : Mji=sumx,y(I(x,y)台j句i)
        // where I(x,y) is the intensity of the pixel (x, y).
        double momX10 = cvGetSpatialMoment(moments, 1, 0); // (x,y)
        double momY01 = cvGetSpatialMoment(moments, 0, 1);// (x,y)
        double area = cvGetCentralMoment(moments, 0, 0);
        
        posX = (int) (momX10 / area);
        posY = (int) (momY01 / area);

        return new Dimension(posX, posY);
    }
	
	static double getArea(IplImage image) {

        CvMoments moments = new CvMoments();
        cvMoments(image, moments, 1);
        // cv Spatial moment : Mji=sumx,y(I(x,y)台j句i)
        // where I(x,y) is the intensity of the pixel (x, y).
        double area = cvGetCentralMoment(moments, 0, 0);
        return area;
	}
	
	static double getRoundness(CvSeq ptr) {
        CvMoments moments = new CvMoments();
        cvMoments(ptr, moments, 1);
        // cv Spatial moment : Mji=sumx,y(I(x,y)台j句i)
        // where I(x,y) is the intensity of the pixel (x, y).
        double a = cvGetCentralMoment(moments, 2, 0);
        double b = cvGetCentralMoment(moments, 0, 2);
        double c = cvGetCentralMoment(moments, 1, 1);
        double num = a+b+Math.sqrt(Math.pow((a-b), 2)+4*Math.pow(c, 2));
        double den = a+b-Math.sqrt(Math.pow((a-b), 2)+4*Math.pow(c, 2));
        return den/num;
	}

	static IplImage hsvThreshold(IplImage orgImg, Color color) {
        // 8-bit, 3- color =(RGB)
    	IplImage imgHSV = cvCreateImage(cvGetSize(orgImg), 8, 3);
        System.out.println(cvGetSize(orgImg));
        cvCvtColor(orgImg, imgHSV, CV_BGR2HSV);
        // 8-bit 1- color = monochrome
        IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);

        if (color.equals(Color.red)){
    		// cvScalar : ( H , S , V, A)
    		cvInRangeS(imgHSV, cvScalar(160, 145, 50, 0), cvScalar(180, 255, 255, 0), imgThreshold);
    	} else if (color.equals(Color.blue)){
            // cvScalar : ( H , S , V, A)
            cvInRangeS(imgHSV, cvScalar(100, 50, 20, 0), cvScalar(130, 255, 100, 0), imgThreshold);
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
}