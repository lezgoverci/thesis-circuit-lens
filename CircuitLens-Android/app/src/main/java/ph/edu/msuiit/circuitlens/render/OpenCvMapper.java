package ph.edu.msuiit.circuitlens.render;

import org.opencv.core.CvType;
import org.opencv.core.MatOfDouble;

import java.util.Vector;

/**
 * Created by vercillius on 5/31/2016.
 */
public class OpenCvMapper implements Mapper {


    private MatOfDouble mProjectionMatrix;      // A 4x4 transformation matrix
    private MatOfDouble mCameraMatrix;          // A 3x3 camera matrix
    private boolean mProjectionMatrixDirty = true;
    private boolean mCameraMatrixDirty = true;
    private int mCameraWidthPx;
    private int mCameraHeightPx;
    private float mHorizontalFOV;
    private float mVerticalFOV;
    private double mNear = 0.1;
    private double mFar = 10;

    public void setCameraWidth(int width){
        mCameraWidthPx = width;
    }

    public void setCameraHeight(int height){
        mCameraHeightPx = height;
    }

    public void setCameraHorizontalFOV(float horizontalFOV){
        mHorizontalFOV = horizontalFOV;
    }

    public void setCameraVerticalFOV(float verticalFOV){
        mVerticalFOV = verticalFOV;
    }


    public double getNearClippingPlane(){
        return mNear;
    }

    public double getFarClippingPlane(){  return mFar;  }

    @Override
    public Vector getRotation() {
        return null;
    }

    @Override
    public double getAxisRotationX() {
        return 0;
    }

    @Override
    public double getAxisRotationY() {
        return 0;
    }

    @Override
    public double getAxisRotationZ() {
        return 0;
    }

    @Override
    public Vector getTranslation() {
        return null;
    }

    @Override
    public double getTranslationX() {
        return 0;
    }

    @Override
    public double getTranslationY() {
        return 0;
    }

    @Override
    public double getTranslationZ() {
        return 0;
    }

    @Override
    public Vector getScaleTransformation() {
        return null;
    }

    @Override
    public double getScaleTransformationX() {
        return 0;
    }

    @Override
    public double getScaleTransformationY() {
        return 0;
    }

    @Override
    public double getScaleTransformationZ() {
        return 0;
    }

    @Override
    public void setPixelDensity(int pixelDensity) {

    }

    @Override
    public int getPixelDensity() {
        return 0;
    }


    @Override
    public double[] getTransformationMatrix() {
        return new double[0];
    }

    @Override
    public MatOfDouble getCameraProjectionMatrix() {
        if(mCameraMatrixDirty){
            if(mCameraMatrix == null){
                mCameraMatrix = new MatOfDouble();
                mCameraMatrix.create(3,3, CvType.CV_64FC1);
            }
            final float fovAspectRatio = mHorizontalFOV / mVerticalFOV;
            final double diagonalPx = Math.sqrt((Math.pow(mCameraWidthPx,2.0) + Math.pow(mCameraWidthPx / fovAspectRatio,2.0)));
            final double focalLengthPx = 0.5 * diagonalPx / Math.sqrt(
                    Math.pow(Math.tan(0.5 * mHorizontalFOV * Math.PI / 180f), 2.0) +
                            Math.pow(Math.tan(0.5 * mVerticalFOV * Math.PI / 180f), 2.0)
            );

            mCameraMatrix.put(0, 0, focalLengthPx);
            mCameraMatrix.put(0, 1, 0.0);
            mCameraMatrix.put(0, 2, 0.5 * mCameraWidthPx);
            mCameraMatrix.put(1, 0, 0.0);
            mCameraMatrix.put(1, 1, focalLengthPx);
            mCameraMatrix.put(1, 2, 0.5 * mCameraHeightPx);
            mCameraMatrix.put(2, 0, 0.0);
            mCameraMatrix.put(2, 1, 0.0);
            mCameraMatrix.put(2, 2, 1.0);
        }

        return mCameraMatrix;

    }


}
