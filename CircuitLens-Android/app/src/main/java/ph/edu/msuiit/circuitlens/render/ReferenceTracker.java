package ph.edu.msuiit.circuitlens.render;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;

/**
 * Created by vercillius on 6/1/2016.
 */
public class ReferenceTracker {

    // Bounding Box
    private MatOfPoint2f mBoundingBoxPoints2D = new MatOfPoint2f();
    private Mat mBoundingBoxCorners = new Mat(4,1, CvType.CV_32FC2); // To be used in finding the perspective transform
    private MatOfPoint3f mBoundingBoxPoints3d;          // To be used in SolvePNP function to generate transformations

    // Convex Hull
    private MatOfPoint2f mApproxConvexHullPoints2D;     // To be used in finding the homography

    public ReferenceTracker(){
        mBoundingBoxPoints3d = new MatOfPoint3f();
        mApproxConvexHullPoints2D = new MatOfPoint2f();
    }

    public ReferenceTracker(MatOfPoint2f hullPoints2D){
        mBoundingBoxPoints3d = new MatOfPoint3f();
        mApproxConvexHullPoints2D = hullPoints2D;
    }

    // Used for SolvePNP to generate transformations
    public MatOfPoint3f getBoundingBoxPoints3D(){
        //TODO initialize the box in 3D
        return mBoundingBoxPoints3d;
    }

    // Used for findHomography
    public MatOfPoint2f getApproxConvexHullPoints2D(){
        return mApproxConvexHullPoints2D;
    }

    public void setApproxConvexHullPoints2D(MatOfPoint2f points){
        mApproxConvexHullPoints2D = points;
    }

}
