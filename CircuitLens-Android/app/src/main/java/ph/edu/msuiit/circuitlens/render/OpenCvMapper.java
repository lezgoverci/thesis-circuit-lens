package ph.edu.msuiit.circuitlens.render;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

/**
 * Created by vercillius on 5/31/2016.
 */
public class OpenCvMapper implements Mapper {

    private static ReferenceTracker mReference;

    private static Mat mImg;
    private static Mat mHomography;
    private static Mat mCurrentFrameBoxCorners;
    private static boolean isSetTracking = false;
    private static boolean isHomographyFound = false;

    private MatOfDouble mProjectionMatrix;      // A 4x4 transformation matrix
    private boolean mProjectionMatrixDirty = true;

    private static MatOfPoint2f mCurrentFrameBoxPoints2D;

    private static double[] mOrientation = new double[3];
    private static double[] mTranslation = new double[3];
    private static double[] mScaling = new double[3];

    private static MatOfDouble   mRVec;     // The Euler angles of the detected target.
    private static MatOfDouble   mTVec;


    private int mPixelDensity;
    private static MatOfDouble mCameraMatrix;
    private static MatOfDouble mDistortion;


    public void onResume(){
        mReference = new ReferenceTracker();
        mCurrentFrameBoxCorners = new Mat(4,1, CvType.CV_32FC2);
        mCurrentFrameBoxPoints2D = new MatOfPoint2f();
        mRVec = new MatOfDouble();
        mTVec = new MatOfDouble();
    }

    public void setImg(Mat img){
        mImg = img;
    }

    public void setCamera(MatOfDouble cameraMatrix, MatOfDouble distortion){
        mCameraMatrix = cameraMatrix;
        mDistortion = distortion;
    }

    public boolean isHomographyFound(){
        return isHomographyFound;
    }

    public static void map(boolean isTakePhoto) {
        if(mImg != null){
            Mat mProcessedImg = ImagePreprocessor.getProcessedImage(mImg);                  // preprocess img
            MatOfPoint2f approxHullPoints = PointsExtractor.getPoints2D(mProcessedImg);     // find approx convex hull points in 2D
            if(approxHullPoints.empty()){
                return;
            }
            drawConvexHull(approxHullPoints); //for debug purposes

            if(isTakePhoto){ // update the tracking reference
                mReference.setConvexHullPoints(PointsExtractor.getConvexHullPoints());      // setting the raw convex hull
                mReference.setApproxConvexHullPoints2D(approxHullPoints);                   // setting the approx convex hull
                mReference.setBoundingBoxCorners();
                mReference.setBoundingBoxPoints3D();
                isSetTracking = true;
            }
            if(isSetTracking){
                updateHomography(approxHullPoints); // update homography using the current convex hull points
                if(isHomographyFound){
                    //isHomographyFound = false;
                    getCurrentFrameBoxCorners();    // get current frame box corners
                    updateCurrentFrameBox2D();      // update current frame box points 2D
                    updateTransformations();        // update transformations
                    drawBoundingBox();
                }
            }

        }
    }

    private static void drawBoundingBox() {
        List<Point> points = mCurrentFrameBoxPoints2D.toList();
        Imgproc.line(mImg,points.get(0),points.get(1),new Scalar(255,0,0));
        Imgproc.line(mImg,points.get(1),points.get(2),new Scalar(255,0,0));
        Imgproc.line(mImg,points.get(2),points.get(3),new Scalar(255,0,0));
        Imgproc.line(mImg,points.get(3),points.get(0),new Scalar(255,0,0));
    }

    private static void drawConvexHull(MatOfPoint2f approxHullPoints) {
        List<Point> points = approxHullPoints.toList();
        for(int i =0; i < points.size() - 1; i++){
            Imgproc.line(mImg,points.get(i),points.get(i + 1),new Scalar(0,255,0),5);
        }
        Imgproc.line(mImg,points.get(points.size() - 1),points.get(0),new Scalar(0,255,0),5);
    }

    private static void updateTransformations() {
        Calib3d.solvePnP(mReference.getBoundingBoxPoints3D(),mCurrentFrameBoxPoints2D,mCameraMatrix,mDistortion,mRVec,mTVec);
    }

    private static void getCurrentFrameBoxCorners() {
        Core.perspectiveTransform(mReference.getBoundingBoxCorners(),mCurrentFrameBoxCorners,mHomography); // output is saved in mCurrentFrameBoxCorners
    }

    private static void updateCurrentFrameBox2D() {
        final double[] trackingImageBoxCorner0 = mCurrentFrameBoxCorners.get(0,0);
        final double[] trackingImageBoxCorner1 = mCurrentFrameBoxCorners.get(1,0);
        final double[] trackingImageBoxCorner2 = mCurrentFrameBoxCorners.get(2,0);
        final double[] trackingImageBoxCorner3 = mCurrentFrameBoxCorners.get(3,0);
        mCurrentFrameBoxPoints2D.fromArray(
                new Point(trackingImageBoxCorner0[0],trackingImageBoxCorner0[1]),
                new Point(trackingImageBoxCorner1[0],trackingImageBoxCorner1[1]),
                new Point(trackingImageBoxCorner2[0],trackingImageBoxCorner2[1]),
                new Point(trackingImageBoxCorner3[0],trackingImageBoxCorner3[1])
        );
    }

    private static void updateHomography(MatOfPoint2f approxHullPoints) {
        //TODO check if the reference points and the current frame points correspond
        if(mReference.getApproxConvexHullPoints2D().toList().size() == approxHullPoints.toList().size()){
            mHomography = Calib3d.findHomography(mReference.getApproxConvexHullPoints2D(),approxHullPoints,Calib3d.RANSAC,40.0);
            isHomographyFound = true;
        }
    }


    @Override
    public double[] getOrientation() {
        //TODO check if elements correspond to correct roll, yaw, pitch
        mOrientation[0] = mRVec.toArray()[0];
        mOrientation[1] = mRVec.toArray()[1];
        mOrientation[2] = mRVec.toArray()[2];
        return mOrientation;
    }

    @Override
    public double getOrientationRoll() {
        //TODO check if first element is for roll
        return mOrientation[0] = mRVec.toArray()[2];
    }

    @Override
    public double getOrientationYaw() {
        //TODO check if second element is for yaw
        return mOrientation[1] = mRVec.toArray()[1];
    }

    @Override
    public double getOrientationPitch() {
        //TODO check if third element is for pitch
        return mOrientation[2] = mRVec.toArray()[0];
    }

    @Override
    public double[] getTranslation() {
        mTranslation[0] = mTVec.toArray()[0];
        mTranslation[1] = mTVec.toArray()[1];
        mTranslation[2] = mTVec.toArray()[2];
        return mTranslation;
    }

    @Override
    public double getTranslationX() {
        return mTranslation[0] = mTVec.toArray()[0];
    }

    @Override
    public double getTranslationY() {
        return mTranslation[1] = mTVec.toArray()[1];
    }

    @Override
    public double getTranslationZ() {
        return mTranslation[2] = mTVec.toArray()[2];
    }

    @Override
    public double[] getScaleTransformation() {
        return mScaling;
    }

    @Override
    public double getScaleTransformationX() {
        return mScaling[0];
    }

    @Override
    public double getScaleTransformationY() {
        return mScaling[1];
    }

    @Override
    public double getScaleTransformationZ() {
        return mScaling[2];
    }

    @Override
    public void setPixelDensity(int pixelDensity) {
        mPixelDensity = pixelDensity;
    }

    @Override
    public int getPixelDensity() {
        return mPixelDensity;
    }

    @Override
    public double[] getTransformationMatrix() {
        return new double[0];
    }


    public void reset() {

    }

    public void drawDebug() {

    }
}
