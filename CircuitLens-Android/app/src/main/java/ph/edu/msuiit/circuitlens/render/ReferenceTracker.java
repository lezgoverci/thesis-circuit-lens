package ph.edu.msuiit.circuitlens.render;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;

/**
 * Created by vercillius on 6/1/2016.
 */
public class ReferenceTracker {
    private MatOfPoint2f mApproxConvexHullPoints2D;
    private static MatOfPoint2f mPoints2D;

    // Bounding Box
    private MatOfPoint2f mBoundingBoxPoints2D = new MatOfPoint2f();
    private Mat mBoundingBoxCorners = new Mat(4,1, CvType.CV_32FC2);

    public ReferenceTracker(){
        mApproxConvexHullPoints2D = new MatOfPoint2f();
    }

    public ReferenceTracker(MatOfPoint2f points){
        mApproxConvexHullPoints2D = points;
    }
}
