package vision; 

import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvInRangeS;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MEDIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetSpatialMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetCentralMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMoments;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

import map.Map;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;

import core.Block;
import core.Config;
import core.Config.BlockColor;

import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.CV_FILLED;
import static com.googlecode.javacv.cpp.opencv_core.CV_RGB;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvDrawContours;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_CCOMP;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvBoundingRect;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;

public class ObjectPositionDetect{

	private static ObjectPositionDetect instance;
	private int posX;
	private int posY;
	private double momX10;
	private double momY01;
	private double area;
	public ArrayList<Block> blocks = new ArrayList<Block>();
	ArrayList<Block> redBlocks = new ArrayList<Block>();
	ArrayList<Block> blueBlocks = new ArrayList<Block>();
	ArrayList<Block> greenBlocks = new ArrayList<Block>();
	ArrayList<Block> yellowBlocks = new ArrayList<Block>();
	//CanvasFrame blocksCanvas = new CanvasFrame("Blocks Canvas");
    long lastTime;

	CanvasFrame canvas2 = new CanvasFrame("Block Detection");
	JFrame listener; 
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
	private Block block;
	private Dimension position;
	public boolean ready;
	CaptureImage capture;
	private IplImage orgImg;
	private IplImage thresholdImage;
	private Preferences configs;

	private int redHueMax;
	private int redHueMin;
	private int redSatMin;
	private int redSatMax;
	private int redValMin;
	private int redValMax;
	private int blueHueMin;
	private int blueHueMax;
	private int blueSatMin;
	private int blueSatMax;
	private int blueValMin;
	private int blueValMax;
	private int greenHueMin;
	private int greenHueMax;
	private int greenSatMin;
	private int greenSatMax;
	private int greenValMin;
	private int greenValMax;
	private int yellowHueMin;
	private int yellowHueMax;
	private int yellowSatMin;
	private int yellowSatMax;
	private int yellowValMin;
	private int yellowValMax;
	
	public ObjectPositionDetect(){
    	//lastTime = System.currentTimeMillis();
    	capture = new CaptureImage();
    	listener = new JFrame();
    	//KeyConfigs keyConfigs = new KeyConfigs();
    	listener.addKeyListener(new KeyConfigs());
        //listener.setPreferredSize(new Dimension(9000 + 1, 9000 + 32));
        listener.setVisible(true);
		//canvas2.addKeyListener(new KeyConfigs());
    	configs = Preferences.userRoot();	
		updateConfig();
     	//capture.start();
		/*
    	while (!capture.ready){
    		try {Thread.sleep((long) 0.0001);} catch (InterruptedException e) {}
    	}
    	ready = true;
		*/
	}
	
	
    public void run() {
    	while (true){
    		step();
    	}
    }
    
    public void step(){
    	capture.step();
	    if (capture.img != null){
		        //orgImg = capture.img.clone();
	    		cvFlip(capture.img,capture.img,0);
	    		cvFlip(capture.img,capture.img,1);
	    		blocks.clear();
	    		blueBlocks.clear();
	    		redBlocks.clear();
	    		yellowBlocks.clear();
	    		greenBlocks.clear();
		        lastTime = System.currentTimeMillis();
		        capture.img = getBlocks(BlockColor.RED, capture.img);
		        //capture.img = getBlocks(BlockColor.BLUE, capture.img);
		        //capture.img = getBlocks(BlockColor.GREEN, capture.img);
		        //capture.img = getBlocks(BlockColor.YELLOW, capture.img);
//		        /System.out.println(System.currentTimeMillis()-lastTime);
		        
		        canvas2.showImage(capture.img);
	    }
    }
private IplImage getBlocks(BlockColor color, IplImage orgImg) {
    
   thresholdImage = hsvThreshold(orgImg, color);
   
	//blocksCanvas.showImage(thresholdImage);
    //cvSaveImage("hsvthreshold2.jpg", thresholdImage);
    
    contours = new CvSeq();

    ptr = new CvSeq();
    mem = cvCreateMemStorage(0);

    cvFindContours(thresholdImage, mem, contours, sizeof(CvContour.class) , CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
    
    if(contours.isNull()){
    	cvReleaseMemStorage(mem);
        cvReleaseImage(thresholdImage);
    	
    	return orgImg;
	}
    
    for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
    	
    	round = getRoundness(ptr);
        if (round < 0.3 || getArea(ptr)<100){
        	continue;
        }
        
        position = getCoordinates(thresholdImage, orgImg);
        block = new Block(position.width, position.height, ptr.total(), color);
        System.out.println("Dimension of original Image : " + thresholdImage.width() + " , " + thresholdImage.height());
        System.out.println("Position of  spot    : x : " + position.width + " , y : " + position.height);
        
        block.setPosition(Map.getInstance().bot.pose);
        block.updateRelXY(orgImg.width());
        
        //print out the i,j,relx,rely 
//        System.out.println("position of i: "+block.i); 
//        System.out.println("position of j: "+block.j);
//        System.out.println("relx : "+block.relX);
//        System.out.println("rely : "+block.relY);
        blocks.add(block);

        Color actualColor;
        
        switch (color){
        case BLUE:
        	blueBlocks.add(block);
        	actualColor = Color.BLUE;
        	break;
        case RED:
        	redBlocks.add(block);
        	actualColor = Color.RED;
        	break;
        case GREEN:
        	greenBlocks.add(block);
        	actualColor = Color.GREEN;
        	break;
        case YELLOW:
        	yellowBlocks.add(block);
        	actualColor = Color.YELLOW;
        	break;
        default:
        	actualColor = Color.WHITE;
        		
        }
        
        sColor = CV_RGB( actualColor.getRed(), actualColor.getGreen(), actualColor.getBlue());
        cvDrawContours(orgImg, ptr, sColor, CV_RGB(0,0,0), -1, CV_FILLED, 8, cvPoint(0,0));
    	boundbox = cvBoundingRect(ptr, 0);
       // cvRectangle( orgImg, cvPoint( boundbox.x(), boundbox.y() ), cvPoint( boundbox.x() + boundbox.width(), boundbox.y() + boundbox.height()),cvScalar( 0, 255, 255, 0 ), 3, 0, 0 ); 
         	
        //System.out.println("Roundness of "+color.toString()+" spot    : " + round);
    
    }
    
    cvReleaseMemStorage(mem);
    cvReleaseImage(thresholdImage);
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

//        System.out.println("position of x is: " + posX);
//        System.out.println("position of y is: "+posY);
//        System.out.println("area of block is: "+area); 
//        
        return new Dimension(posX, posY);
        

    }
	

	//CHANGE THE CONSTANTS IN THIS
	int sizePtoDistance(int sizeP) {
		return Config.m*sizeP + Config.c; 
	}
	
	double getArea(IplImage image) {

        CvMoments moments = new CvMoments();
        cvMoments(image, moments, 1);
        // cv Spatial moment : Mji=sumx,y(I(x,y)台j句i)
        // where I(x,y) is the intensity of the pixel (x, y).
        area = cvGetCentralMoment(moments, 0, 0);
        return area;
	}
	
	//fill out area to distance, knowing block size 
	double areaToDistance(double area) {
//	    Config.blockSize;
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

	IplImage hsvThreshold(IplImage orgImg, BlockColor color) {
        // 8-bit, 3- color =(RGB)
    	imgHSV = cvCreateImage(cvGetSize(orgImg), 8, 3);
        //System.out.println(cvGetSize(orgImg));
        cvCvtColor(orgImg, imgHSV, CV_BGR2HSV);
        // 8-bit 1- color = monochrome
        imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
        if (color.equals(BlockColor.RED)){
    		// cvScalar : ( H , S , V, A)
//    		/System.out.println("RED RANGE: "+redHueMin+" "+redHueMax+" "+redSatMin+" "+redSatMax+" "+redValMin+" "+redValMax+" ");
    		cvInRangeS(imgHSV, cvScalar(0, 80, 100, 0), cvScalar(10, 255, 255, 0), imgThreshold);
    		cvInRangeS(imgHSV, cvScalar(redHueMin, redSatMin, redValMin, 0), cvScalar(redHueMax, redSatMax, redValMax, 0), imgThreshold);
    		
    	} else if (color.equals(BlockColor.BLUE)){
            // cvScalar : ( H , S , V, A)
    		cvInRangeS(imgHSV, cvScalar(blueHueMin, blueSatMin, blueValMin, 0), cvScalar(blueHueMax, blueSatMax, blueValMax, 0), imgThreshold);
    		//System.out.println("BLUE RANGE: "+blueHueMin+" "+blueHueMax +" "+blueSatMin+" "+blueSatMax+" "+blueValMin+" "+blueValMax+" ");
    		 
        } else if (color.equals(BlockColor.GREEN)){
            // cvScalar : ( H , S , V, A)
    		cvInRangeS(imgHSV, cvScalar(greenHueMin, greenSatMin, greenValMin, 0), cvScalar(greenHueMax, greenSatMax, greenValMax, 0), imgThreshold);
    		//System.out.println("GREEN RANGE: "+greenHueMin+" "+greenHueMax+" "+greenSatMin+" "+greenSatMax+" "+greenValMin+" "+greenValMax+" ");
        } else if (color.equals(BlockColor.YELLOW)){
            // cvScalar : ( H , S , V, A)  
    		cvInRangeS(imgHSV, cvScalar(yellowHueMin, yellowSatMin, yellowValMin, 0), cvScalar(yellowHueMax, yellowSatMax, yellowValMax, 0), imgThreshold);
    		//System.out.println("YELLOW RANGE: "+yellowHueMin+" "+yellowHueMax+" "+yellowSatMin+" "+yellowSatMax+" "+yellowValMin+" "+yellowValMax+" ");
        }
    	cvReleaseImage(imgHSV);
        cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 13);
        // save
        return imgThreshold;
    }
	
	void updateConfig(){
			redHueMin = configs.getInt("RedHueMin", 180);
			redHueMax = configs.getInt("RedHueMax", 255);
			redSatMin = configs.getInt("RedSatMin", 180);
			redSatMax = configs.getInt("RedSatMax", 255);
			redValMin = configs.getInt("RedValMin", 180);
			redValMax = configs.getInt("RedValMax", 255);
			blueHueMin = configs.getInt("BlueHueMin", 0);
			blueHueMax = configs.getInt("BlueHueMax", 10);
			blueSatMin = configs.getInt("BlueSatMin", 180);
			blueSatMax = configs.getInt("BlueSatMax", 255);
			blueValMin = configs.getInt("BlueValMin", 180);
			blueValMax = configs.getInt("BlueValMax", 255);
			greenHueMin = configs.getInt("GreenHueMin", 105);
			greenHueMax = configs.getInt("GreenHueMax", 140);
			greenSatMin = configs.getInt("GreenSatMin", 180);
			greenSatMax = configs.getInt("GreenSatMax", 255);
			greenValMin = configs.getInt("GreenValMin", 180);
			greenValMax = configs.getInt("GreenValMax", 255);
			yellowHueMin = configs.getInt("YellowHueMin", 20);
			yellowHueMax = configs.getInt("YellowHueMax", 40);
			yellowSatMin = configs.getInt("YellowSatMin", 180);
			yellowSatMax = configs.getInt("YellowSatMax", 255);
			yellowValMin = configs.getInt("YellowValMin", 180);
			yellowValMax = configs.getInt("YellowValMax", 255);
	}
	


	public static void main(String[] Args){
		ObjectPositionDetect me = getInstance();
		
		me.run();
	}


	public static ObjectPositionDetect getInstance() {
		// TODO Auto-generated method stub
		 if (instance == null)
	            instance = new ObjectPositionDetect();
	        return instance;   
	}
}
