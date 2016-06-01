package ph.edu.msuiit.circuitlens.render;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vercillius on 6/1/2016.
 */
public class PointsExtractor {
    private static Mat mImageGray;
    private static MatOfPoint2f mPoints2D;


    // Bounding Box
    private MatOfPoint2f mBoundingBoxPoints2D = new MatOfPoint2f();
    private Mat mBoundingBoxCorners = new Mat(4,1, CvType.CV_32FC2);

    // Contours
    private List<MatOfPoint> mContoursList = new ArrayList<>();
    private MatOfPoint mLargestContour = new MatOfPoint();
    private int mIndexLargestContour;

    // Convex Hull
    private MatOfPoint mConvexHull = new MatOfPoint();
    private MatOfPoint2f mApproxHullPoints2D = new MatOfPoint2f();

    public static void setImage(Mat img){
        mImageGray = new Mat();
        mImageGray = img;
        mPoints2D = new MatOfPoint2f();
    }

    public static MatOfPoint2f getPoints2D(){
        // Find all contours
        // Find the largest contour
        // Find the convex hull of the largest contour
        // Approximate convex hull
        // Get corners of the approximated convex hull
        // Get the 2D points of the approximated convex hull
        return mPoints2D;
    }

}
