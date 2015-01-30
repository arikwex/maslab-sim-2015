package vision;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class FrameProcessor {

	public static final int IMG_HEIGHT = 240;
	public static final int IMG_WIDTH = 320;
	public static final double CAMERA_FOV = 70.0; //in degrees
	
	//following vectors are used for HSV filtering
	private static final Scalar lowerRed = new Scalar(110, 140, 10);
	private static final Scalar upperRed = new Scalar(130, 255, 255);
	
	private static final Scalar lowerGreen = new Scalar(35, 80, 10);
	private static final Scalar upperGreen = new Scalar(60, 255, 255);
	
	private static final int yBound = 30; // hack to ignore top pixels
	
	
	
	private static final int contourAreaThresh = 15;
	private static final int cleanKernelSize = 5;
	private static final int numBuffers = 6;
	
	private List<Mat> buffers = null;
	private Mat cleanKernel;
	
	public FrameProcessor() {
		buffers = new ArrayList<Mat>();
		for (int i = 0; i < numBuffers; i++) {
			buffers.add(new Mat());
		}
		
		cleanKernel = Mat.ones(cleanKernelSize, cleanKernelSize, CvType.CV_8U);
	}
	
	public List<Block> getBlocks(Mat frame) {
		Imgproc.resize(frame, buffers.get(0), new Size(IMG_WIDTH, IMG_HEIGHT));
		//first convert to hsv, store it in the first buffer
		Imgproc.cvtColor(buffers.get(0), buffers.get(1), Imgproc.COLOR_BGR2HSV);
		Core.inRange(buffers.get(1), lowerRed, upperRed, buffers.get(2));
		Core.inRange(buffers.get(1), lowerGreen, upperGreen, buffers.get(3));
		Imgproc.morphologyEx(buffers.get(2), buffers.get(0), Imgproc.MORPH_OPEN, cleanKernel); // Red
		Imgproc.morphologyEx(buffers.get(3), buffers.get(1), Imgproc.MORPH_OPEN, cleanKernel); // Green
		
		List<Block> allBlocks = new ArrayList<Block>();
		allBlocks.addAll(findColorBlobs(buffers.get(0), Block.Color.Red));
		allBlocks.addAll(findColorBlobs(buffers.get(1), Block.Color.Green));
		return allBlocks;
	}
	
	public List<Block> findColorBlobs(Mat binaryImg, Block.Color c) {
		double doubleBlockRatio = 1.75; //if height is this much more than wid, prolly 2
		double tripleBlockRatio = 2.5; // same, but for 3 blocks
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(binaryImg, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		List<Block> blobs = new ArrayList<Block>();
		for (MatOfPoint cnt : contours) {
			double area = Imgproc.contourArea(cnt);
//			System.out.println("Area: " + area);
			if (area > contourAreaThresh) {
				Rect bound = Imgproc.boundingRect(cnt);
//				double x = bound.x + (bound.width / 2.0);
//				double y = bound.y + (bound.height / 2.0);
				if (bound.y + bound.height > yBound) {
					// check if this is two blocks
					if (bound.width * tripleBlockRatio < bound.height) {
						// three blocks here
						double height = bound.height / 3.0;
						blobs.add(new Block(c, bound.x, bound.y, bound.width, height, area));
						blobs.add(new Block(c, bound.x, bound.y+height, bound.width, height, area));
						blobs.add(new Block(c, bound.x, bound.y+2*height, bound.width, height, area));
					} else if (bound.width * doubleBlockRatio < bound.height) {
						double height = bound.height / 2.0;
						blobs.add(new Block(c, bound.x, bound.y, bound.width, height, area));
						blobs.add(new Block(c, bound.x, bound.y+height, bound.width, height, area));
					} else {
						blobs.add(new Block(c, bound.x, bound.y, bound.width, bound.height, area));
					}
					
				}
			}
		}
		return blobs;
	}
	
	
	static {
		BlockFinder.initialize();
	}
	
	/**
	 * main method for testing
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
