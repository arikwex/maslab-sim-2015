package vision;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

/**
 * Serves as the entry point for vision code
 * @author akhil
 *
 */
public class BlockFinder {

	private static final int DEFAULT_CAM = 1;
	private static final boolean debug = true;
	
	private static final int frameBurnCount = 6; // number of frames to burn each time 
	
	private VideoCapture camera;
	private FrameProcessor fp;
	private JLabel cameraPane;
	
	public BlockFinder() {
		//initialize();
		
		// Set up the camera
		camera = new VideoCapture();
		camera.open(DEFAULT_CAM);
		fp = new FrameProcessor();
		
		if (debug) {
			int width = (int) (camera.get(Highgui.CV_CAP_PROP_FRAME_WIDTH));
			int height = (int) (camera.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT));
			cameraPane = createWindow("Camera output", width, height);
//			JLabel opencvPane = createWindow("OpenCV output", width, height);
		}
		
	}
	
	/**
	 * Finds the closest block stack and returns its angular position
	 * and colors.
	 * 
	 * Returns null if there are no blocks
	 * 
	 * @return [angle (a float as a string), color of top block, color of next block, ...]
	 */
	public String[] findStack() {
		
		
		Mat frame = getNewFrame(); //takes care of burning the frames;
		
		if (debug) {
			Mat2Image rawImageConverter = new Mat2Image(BufferedImage.TYPE_3BYTE_BGR);
			updateWindow(cameraPane, frame, rawImageConverter);
		}
		
		List<Block> allBlocks = fp.getBlocks(frame);
		if (allBlocks.size() == 0)
			return null;
		Collections.sort(allBlocks);
		Block closest = allBlocks.get(0);
		List<Block> stack = findStackStartingWithBlock(closest, allBlocks);
		String[] stackString = new String[stack.size() + 1];
		stackString[0] = Double.toString(findAngle(closest.getX(), closest.getY()));
		for (int i = 1; i <= stack.size(); i++) {
			stackString[i] = stack.get(stack.size()-i).getColor().toString();
		}
		return stackString;
	}
	
	
	private Mat getNewFrame() {
		Mat rawImage = new Mat();
		for (int i = 0; i <= frameBurnCount; i++) {
			while (!camera.read(rawImage)) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return rawImage;
	}
	
	private Block getClosestBlock(List<Block> blocks) {
		Block closest = null;
		for (Block b : blocks) {
			if (closest == null || b.getY() > closest.getY()) {
				closest = b;
			}
		}
		return closest;
	}
	
	/**
	 * Returns a list, from top to bottom, of the Blocks in the stack that
	 * has the given block as its base 
	 * @param bottom
	 * @return
	 */
	private List<Block> findStackStartingWithBlock(Block bottom, List<Block> allBlocks) {
		// game plan, find angle of bottom and say that the next two
		// blocks with a similar angle are in the stack too.
		double stackAngle = findAngle(bottom.getX(), bottom.getY());
		double angleEpsilon = 0.2;
		Collections.sort(allBlocks);
		List<Block> stack = new ArrayList<Block>();
		stack.add(bottom);
		for (Block b : allBlocks) {
			if (b == bottom) {
				continue;
			} else {
				double angle = findAngle(b.getX(), b.getY());
				if (Math.abs(angle - stackAngle) < angleEpsilon) {
					stack.add(b);
					if (stack.size() == 3) {
						return stack;
					}
				}
			}
		}
		return stack;
	}
	
	/**
	 * Find the angle to the block.
	 * @param x is the coordinate in the image of the block's center
	 * @return angle. 0 = center. Pos angles to the left.
	 */
	private double findAngle(double x, double y) {
		//TODO: fix the geometry of the following calculations
		// for now hard code some angles
		// FOV = 70 deg. Assume a linear transform
		return Math.toRadians((x - (FrameProcessor.IMG_WIDTH / 2.0))
				/ FrameProcessor.IMG_WIDTH * FrameProcessor.CAMERA_FOV);
	}
	
	 private static JLabel createWindow(String name, int width, int height) {    
        JFrame imageFrame = new JFrame(name);
        imageFrame.setSize(width, height);
        imageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JLabel imagePane = new JLabel();
        imagePane.setLayout(new BorderLayout());
        imageFrame.setContentPane(imagePane);
        
        imageFrame.setVisible(true);
        return imagePane;
	 }
	    
	private static void updateWindow(JLabel imagePane, Mat mat, Mat2Image converter) {
		int w = (int) (mat.size().width);
		int h = (int) (mat.size().height);
		if (imagePane.getWidth() != w || imagePane.getHeight() != h) {
			imagePane.setSize(w, h);
		}
		BufferedImage bufferedImage = converter.getImage(mat);
		imagePane.setIcon(new ImageIcon(bufferedImage));
	}
	
	/**
	 * Initializes the OpenCV stuff. need to run this before any of the other stuff.
	 */
	public static void initialize() {
		if (debug) {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		} else {
			try {
		        InputStream in = null;
		        File fileOut = null;
		        String osName = System.getProperty("os.name");
		        System.out.println(osName);
		        if(osName.startsWith("Windows")){
		            int bitness = Integer.parseInt(System.getProperty("sun.arch.data.model"));
		            if(bitness == 32){
		                System.out.println("32 bit detected");
		                in = BlockFinder.class.getResourceAsStream("/opencv/x86/opencv_java248.dll");
		                fileOut = File.createTempFile("lib", ".dll");
		            }
		            else if (bitness == 64){
		                System.out.println("64 bit detected");
		                in = BlockFinder.class.getResourceAsStream("/opencv/x64/opencv_java248.dll");
		                fileOut = File.createTempFile("lib", ".dll");
		            }
		            else{
		                System.out.println("Unknown bit detected - trying with 32 bit");
		                in = BlockFinder.class.getResourceAsStream("/opencv/x86/opencv_java248.dll");
		                fileOut = File.createTempFile("lib", ".dll");
		            }
		        }
		        else if(osName.equals("Mac OS X")){
		            in = BlockFinder.class.getResourceAsStream("/opencv/mac/libopencv_java248.dylib");
		            fileOut = File.createTempFile("lib", ".dylib");
		        } else {
		        	// TODO: define stuff for the Edison.
		        }
	
	
		        OutputStream out = FileUtils.openOutputStream(fileOut);
		        if (out == null) System.out.println("out is null");
		        if (in == null) System.out.println("in is null");
		        IOUtils.copy(in, out);
		        in.close();
		        out.close();
		        System.load(fileOut.toString());
		    } catch (Exception e) {
		        throw new RuntimeException("Failed to load opencv native library", e);
		    }
		}	
		
	}
	
	static {
		initialize();
	}
	
	/**
	 * main here just for testing purposes
	 */
	public static void main(String[] args) {
		BlockFinder finder = new BlockFinder();
		while (true) {
			String[] stack = finder.findStack();
			if (stack != null) {
				System.out.print("Angle: " + stack[0] + "\t");
				for (int i = 1; i < stack.length; i++) {
					System.out.print("Block: " + stack[i] + "\t");
				}
				System.out.println("\n");
			}	
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
					
		}

	}

}
