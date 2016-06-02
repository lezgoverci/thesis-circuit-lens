package ph.edu.msuiit.circuitlens.render;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vercillius on 5/31/2016.
 */
public class OpenCvMapper implements Mapper {

    private static ReferenceTracker mReference = new ReferenceTracker();

    private static Mat mImg;
    private static Mat mHomography;
    private static Mat mCurrentFrameBoxCorners = new Mat(4,1, CvType.CV_32FC2);
    private static boolean isSetTracking = false;
    private static boolean isHomographyFound = false;

    private MatOfDouble mProjectionMatrix;      // A 4x4 transformation matrix
    private boolean mProjectionMatrixDirty = true;

    private static MatOfPoint2f mCurrentFrameBoxPoints2D = new MatOfPoint2f();

    private static double[] mRotation = new double[3];
    private static double[] mTranslation = new double[3];
    private static double[] mScaling = new double[3];

    private static MatOfDouble   mRVec = new MatOfDouble();                                  // The Euler angles of the detected target.
    private static MatOfDouble   mTVec = new MatOfDouble();


    private int mPixelDensity;
    private static MatOfDouble mCameraMatrix;
    private static MatOfDouble mDistortion;

    public void setImg(Mat img){
        mImg = img;
    }

    public void setCamera(MatOfDouble cameraMatrix, MatOfDouble distortion){
        mCameraMatrix = cameraMatrix;
        mDistortion = distortion;
    }

    public static void map(boolean isTakePhoto) {
        if(mImg != null){
            // preprocess img
            Mat mProcessedImg = new Mat();
            mProcessedImg = ImagePreprocessor.getProcessedImage(mImg);

            // find convex hull points in 2D
            MatOfPoint2f approxHullPoints = new MatOfPoint2f();
            approxHullPoints = PointsExtractor.getPoints2D(mProcessedImg); // approximated convex hull points 2D

            if(isTakePhoto){
                // update the tracking reference
                mReference.setApproxConvexHullPoints2D(approxHullPoints); // setting the convex hull
                mReference.setBoundingBoxCorners(PointsExtractor.getConvexHullPoints());  // not approx convex hull
                //mReference.setBoundingBoxCorners(approxHullPoints);  // approx convex hull
                mReference.setBoundingBoxPoints3D();
                isSetTracking = true;
            }
            if(isSetTracking){
                // for all succeeding frames,
                // update homography using the current convex hull points
                updateHomography(approxHullPoints);
                if(isHomographyFound){

                    isHomographyFound = false;

                    // get current frame box corners
                    getCurrentFrameBoxCorners();

                    // update current frame box points 2D
                    updateCurrentFrameBox2D();

                    // update transformations
                    updateTransformations();
                }
            }

        }
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

        if(mReference.getApproxConvexHullPoints2D().toList().size() == approxHullPoints.toList().size()){
            mHomography = Calib3d.findHomography(mReference.getApproxConvexHullPoints2D(),approxHullPoints,Calib3d.RANSAC,50.0);
            isHomographyFound = true;
        }
    }

    @Override
    public double[] getRotation() {
        mRotation[0] = mRVec.toArray()[0];
        mRotation[1] = mRVec.toArray()[1];
        mRotation[2] = mRVec.toArray()[2];
        return mRotation;
    }

    @Override
    public double getAxisRotationX() {
        return mRotation[0] = mRVec.toArray()[0];
    }

    @Override
    public double getAxisRotationY() {
        return mRotation[1] = mRVec.toArray()[1];
    }

    @Override
    public double getAxisRotationZ() {
        return mRotation[2] = mRVec.toArray()[2];
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



}
