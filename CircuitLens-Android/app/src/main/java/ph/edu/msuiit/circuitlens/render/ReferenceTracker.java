package ph.edu.msuiit.circuitlens.render;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

/**
 * Created by vercillius on 6/1/2016.
 */
public class ReferenceTracker {

    // Bounding Box
    private Mat mBoundingBoxCorners; // To be used in finding the perspective transform
    private MatOfPoint2f mBoundingBoxPoints2D;
    private MatOfPoint3f mBoundingBoxPoints3D;          // To be used in SolvePNP function to generate transformations

    // Convex Hull
    private MatOfPoint2f mApproxConvexHullPoints2D;     // To be used in finding the homography
    private MatOfPoint mConvexHullPoints;

    private int mCircuitX;
    private int mCircuitY;
    private int mCircuitWidth;
    private int mCircuitHeight;
    private boolean isBoundingBoxCornersSet;

    public ReferenceTracker(){
        mBoundingBoxCorners = new Mat(4,1, CvType.CV_32FC2);
        mBoundingBoxPoints2D = new MatOfPoint2f();
        mBoundingBoxPoints3D = new MatOfPoint3f();
        mApproxConvexHullPoints2D = new MatOfPoint2f();
        isBoundingBoxCornersSet = false;
    }

    public ReferenceTracker(MatOfPoint2f hullPoints2D){
        mBoundingBoxPoints3D = new MatOfPoint3f();
        mApproxConvexHullPoints2D = hullPoints2D;
        isBoundingBoxCornersSet = false;
    }

    // Used for SolvePNP to generate transformations
    public MatOfPoint3f getBoundingBoxPoints3D(){

        return mBoundingBoxPoints3D;
    }

    public void setBoundingBoxPoints3D(){
        if(isBoundingBoxCornersSet){
            mBoundingBoxPoints3D.fromArray(
                    new Point3(mBoundingBoxPoints2D.get(0,0)[0],mBoundingBoxPoints2D.get(0,0)[1],0.0),
                    new Point3(mBoundingBoxPoints2D.get(1,0)[0],mBoundingBoxPoints2D.get(1,0)[1],0.0),
                    new Point3(mBoundingBoxPoints2D.get(2,0)[0],mBoundingBoxPoints2D.get(2,0)[1],0.0),
                    new Point3(mBoundingBoxPoints2D.get(3,0)[0],mBoundingBoxPoints2D.get(3,0)[1],0.0)
            );
        }
    }

    // Used for findHomography
    public MatOfPoint2f getApproxConvexHullPoints2D(){
        return mApproxConvexHullPoints2D;
    }

    public void setApproxConvexHullPoints2D(MatOfPoint2f points){
        mApproxConvexHullPoints2D = points;
    }

    public void setBoundingBoxCorners(){
        Rect box = Imgproc.boundingRect(mConvexHullPoints);
        mCircuitX = box.x;
        mCircuitY = box.y;
        mCircuitWidth = box.width;
        mCircuitHeight = box.height;
        mBoundingBoxCorners.put(0,0,new double[]{mCircuitX,mCircuitY});
        mBoundingBoxCorners.put(1,0,new double[]{mCircuitX + mCircuitWidth ,mCircuitY});
        mBoundingBoxCorners.put(2,0,new double[]{mCircuitX + mCircuitWidth ,mCircuitY + mCircuitHeight});
        mBoundingBoxCorners.put(3,0,new double[]{mCircuitX ,mCircuitY + mCircuitHeight});
        isBoundingBoxCornersSet = true;

    }

    public Mat getBoundingBoxCorners(){
        return mBoundingBoxCorners;
    }

    public void setConvexHullPoints(MatOfPoint convexHullPoints) {
        mConvexHullPoints = convexHullPoints;

    }
}
