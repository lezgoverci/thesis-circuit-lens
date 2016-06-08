package ph.edu.msuiit.circuitlens.render;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by vercillius on 6/1/2016.
 */
public class ImagePreprocessor {

    private static Mat mGrayImg;
    private static Size blurKernel = new Size(5,5);
    private static Mat dilateKernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,new Size(5.0,5.0));

    public static Mat getProcessedImage(Mat img){

        mGrayImg = new Mat();

        // Grayscale
        Imgproc.cvtColor(img,mGrayImg,Imgproc.COLOR_RGB2GRAY);

        // Blur to decrease noise
        Imgproc.blur(mGrayImg,mGrayImg,blurKernel);

        // Find edges
        Imgproc.Canny(mGrayImg,mGrayImg,150,250);

        // emphasize edges by dilating pixels
        Imgproc.dilate(mGrayImg,mGrayImg,dilateKernel);

        return mGrayImg;
    }
}
