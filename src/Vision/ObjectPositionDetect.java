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
import java.awt.Graphics;
import java.util.Random;

import javax.swing.JPanel;
//import java.awt.Graphics;

//import javax.swing.JPanel;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;

import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.CV_FILLED;
import static com.googlecode.javacv.cpp.opencv_core.CV_RGB;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvDrawContours;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_CCOMP;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_BINARY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvThreshold;

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

        for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
            CvScalar color = CV_RGB( Color.red.getRed(), Color.red.getGreen(), Color.red.getBlue());
            cvDrawContours(orgImg, ptr, color, CV_RGB(0,0,0), -1, CV_FILLED, 8, cvPoint(0,0));
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
            CvScalar color = CV_RGB( Color.blue.getRed(), Color.blue.getGreen(), Color.blue.getBlue());
            cvDrawContours(orgImg, ptr, color, CV_RGB(0,0,0), -1, CV_FILLED, 8, cvPoint(0,0));
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
            CvScalar color = CV_RGB( Color.green.getRed(), Color.green.getGreen(), Color.green.getBlue());
            cvDrawContours(orgImg, ptr, color, CV_RGB(0,0,0), -1, CV_FILLED, 8, cvPoint(0,0));
        }

        /*
        IplImage grayImage = cvCreateImage(cvGetSize(orgImg), IPL_DEPTH_8U, 1);
        cvCvtColor(orgImg, grayImage, CV_BGR2GRAY);
        cvThreshold(grayImage, grayImage, 150, 255, CV_THRESH_BINARY);
        */
        cvSaveImage("target.jpg", orgImg);
        CanvasFrame canvas = new CanvasFrame("image");
        canvas.showImage(orgImg);
        /*
        CanvasFrame redcanvas = new CanvasFrame("red");
        IplImage redthresholdImage = hsvThreshold(orgImg, Color.red);
		redcanvas.showImage(redthresholdImage);
        cvSaveImage("hsvthreshold.jpg", redthresholdImage);
        Dimension redposition = getCoordinates(redthresholdImage, orgImg);
        System.out.println("Dimension of original Image : " + redthresholdImage.width() + " , " + redthresholdImage.height());
        System.out.println("Position of red spot    : x : " + redposition.width + " , y : " + redposition.height);
        
        CanvasFrame bluecanvas = new CanvasFrame("blue");
        IplImage bluethresholdImage = hsvThreshold(orgImg, Color.blue);
		bluecanvas.showImage(bluethresholdImage);
        cvSaveImage("hsvthreshold.jpg", bluethresholdImage);
        Dimension blueposition = getCoordinates(bluethresholdImage, orgImg);
        System.out.println("Dimension of original Image : " + bluethresholdImage.width() + " , " + bluethresholdImage.height());
        System.out.println("Position of blue spot    : x : " + blueposition.width + " , y : " + blueposition.height);
        
        CanvasFrame greencanvas = new CanvasFrame("green");
        IplImage greenthresholdImage = hsvThreshold(orgImg, Color.green);
		greencanvas.showImage(greenthresholdImage);
        cvSaveImage("hsvthreshold.jpg", greenthresholdImage);
        Dimension greenposition = getCoordinates(greenthresholdImage, orgImg);
        System.out.println("Dimension of original Image : " + greenthresholdImage.width() + " , " + greenthresholdImage.height());
        System.out.println("Position of green spot    : x : " + greenposition.width + " , y : " + greenposition.height);

        //drawBox(redthresholdImage);
*/
    }
/*
    private static void drawBox(IplImage img) {
        CvMemStorage storage = CvMemStorage.create();
        CvSeq contours = new CvContour(null);

        int noOfContors = cvFindContours(img, storage, contours, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_NONE, new CvPoint(0,0));

        CvSeq ptr = new CvSeq();

        int count =1;
        CvPoint p1 = new CvPoint(0,0),p2 = new CvPoint(0,0);

        for (ptr = contours; ptr != null; ptr = ptr.h_next()) {

            CvScalar color = CvScalar.BLUE;
            CvRect sq = cvBoundingRect(ptr, 0);

                System.out.println("Contour No ="+count);
                System.out.println("X ="+ sq.x()+" Y="+ sq.y());
                System.out.println("Height ="+sq.height()+" Width ="+sq.width());
                System.out.println("");

                p1.x(sq.x());
                p2.x(sq.x()+sq.width());
                p1.y(sq.y());
                p2.y(sq.y()+sq.height());
                cvRectangle(img, p1,p2, CV_RGB(255, 0, 0), 2, 8, 0);
                cvDrawContours(img, ptr, color, CV_RGB(0,0,0), -1, CV_FILLED, 8, cvPoint(0,0));
                count++;

        }

        cvShowImage("contures",img);
        cvWaitKey(0)
        
		
	}
*/
	static Dimension getCoordinates(IplImage thresholdImage, IplImage orgImg) {
        int posX = 0;
        int posY = 0;
        CvMoments moments = new CvMoments();
        cvMoments(thresholdImage, moments, 1);
        // cv Spatial moment : Mji=sumx,y(I(x,y)¥xj¥yi)
        // where I(x,y) is the intensity of the pixel (x, y).
        double momX10 = cvGetSpatialMoment(moments, 1, 0); // (x,y)
        double momY01 = cvGetSpatialMoment(moments, 0, 1);// (x,y)
        double area = cvGetCentralMoment(moments, 0, 0);
        
        posX = (int) (momX10 / area);
        posY = (int) (momY01 / area);

        paint(orgImg,posX,posY);
        return new Dimension(posX, posY);
    }
	/*        
    JPanel jp = new JPanel();
    CanvasFrame path = new CanvasFrame("Object Detection ");
    path.setContentPane(jp);
    Graphics g = jp.getGraphics();
    path.setSize(orgImg.width(), orgImg.height());
        // g.clearRect(0, 0, img.width(), img.height());
    g.setColor(Color.blue);
        // g.fillOval(posX, posY, 20, 20);
    g.drawRect(posX, posY, 25, 25);
       // g.drawOval(posX, posY, 20, 20);
    System.out.println(posX + " , " + posY);
*/
	private static void paint(IplImage img, int posX, int posY) {

		
		/*CanvasFrame path = new CanvasFrame("Object Detection ");
	    JPanel jp = new JPanel();
	    path.setContentPane(jp);
	    	
        Graphics g = jp.getGraphics();
        path.setSize(img.width(), img.height());
        //path.showImage(img);
        
        // g.clearRect(0, 0, img.width(), img.height());
        g.setColor(Color.BLUE);
        // g.fillOval(posX, posY, 20, 20);
        g.drawRect(posX, posY, 25, 25);
       // g.drawOval(posX, posY, 20, 20);
        System.out.println(posX + " , " + posY);
		*/
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
    		cvInRangeS(imgHSV, cvScalar(160, 100, 100, 0), cvScalar(180, 255, 255, 0), imgThreshold);
    	} else if (color.equals(Color.blue)){
            // cvScalar : ( H , S , V, A)
            cvInRangeS(imgHSV, cvScalar(104, 178, 70, 0), cvScalar(130, 255, 255, 0), imgThreshold);
        } else if (color.equals(Color.green)){
            // cvScalar : ( H , S , V, A)
            cvInRangeS(imgHSV, cvScalar(60, 50, 50, 0), cvScalar(100, 255, 255, 0), imgThreshold);
        } else if (color.equals(Color.yellow)){
            // cvScalar : ( H , S , V, A)
            cvInRangeS(imgHSV, cvScalar(25, 150, 100, 0), cvScalar(35, 255, 255, 0), imgThreshold);
        }
    	cvReleaseImage(imgHSV);
        cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 13);
        // save
        return imgThreshold;
    }
}