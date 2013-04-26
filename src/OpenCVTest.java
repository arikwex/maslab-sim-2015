import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class OpenCVTest {
    public static void main(String[] args) {
        System.out.println(System.getProperty("java.library.path"));

        System.loadLibrary("opencv_java245");
        Mat m  = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println("m = " + m.dump());
    }
}