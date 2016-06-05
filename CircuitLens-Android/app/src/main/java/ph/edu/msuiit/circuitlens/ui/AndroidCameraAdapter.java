package ph.edu.msuiit.circuitlens.ui;

import android.hardware.Camera;
import android.os.Build;

import org.opencv.core.CvType;
import org.opencv.core.MatOfDouble;

import java.util.List;

/**
 * Created by vercillius on 5/31/2016.
 */
@SuppressWarnings("deprecation")
public class AndroidCameraAdapter implements CameraAdapter {

    private int mHeightPx = 480;
    private int mWidthPx = 640;
    private int mCameraIndex;
    private int mCameraSizeIndex;
    private int mNumCameras;
    private float mVerticalFOV = 45f; // equivalent in 35mm photography: 28mm lens
    private float mHorizontalFOV = 60f; // equivalent in 35mm photography: 28mm lens
    private double mAspectRatio;
    private boolean mIsCameraBackFacing;
    private boolean mCameraMatrixDirty = true;
    private Camera mCamera;
    private Camera.Parameters mCameraParameters;
    private Camera.Size mImageSize;
    private List<Camera.Size> mSupportedImageSizes; // The image sizes supported by the active camera.
    private MatOfDouble mCameraMatrix;          // A 3x3 camera matrix


    public AndroidCameraAdapter() {
        // Assume device has one only one camera
        // and it is placed at the back pf the device
        mCameraIndex = 0;
        mCameraSizeIndex = 0;
        mNumCameras = 1;
        mIsCameraBackFacing = true;
        getCameraParameters();

    }

    private void getCameraParameters(){

        // Check if the device supports front and back camera.
        // else, use the back camera
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){

            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraIndex,cameraInfo);

            mNumCameras = Camera.getNumberOfCameras();

            if(mNumCameras > 1){

                //Assumed that there is only two cameras
                // present in the device. The front
                // and back cameras only

                mIsCameraBackFacing = (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK);

                if(mIsCameraBackFacing){
                    mCameraIndex++;
                    if(mCameraIndex == mNumCameras){
                        mCameraIndex = 0;
                    }
                }
            }

            mCamera = Camera.open(mCameraIndex);

        } else{
            // Older devices have only 1 camera
            // and it is located at the back
            mCamera.open();
        }

        // Get camera parameters
        mCameraParameters = mCamera.getParameters();
        mCamera.release();

        // Get supported camera preview sizes
        mSupportedImageSizes = mCameraParameters.getSupportedPreviewSizes();
        mImageSize = mSupportedImageSizes.get(mCameraSizeIndex);


    }

    // Call this function on the Activity's onResume function
    public void setCameraParameters(){
        mHorizontalFOV = mCameraParameters.getHorizontalViewAngle();
        mVerticalFOV = mCameraParameters.getVerticalViewAngle();

        mHeightPx = mImageSize.height;
        mWidthPx = mImageSize.width;

        mAspectRatio = (double) mWidthPx / (double) mHeightPx;
    }


    @Override
    public int getCameraWidth() {
        return mWidthPx;
    }

    @Override
    public int getCameraHeight() {
        return mHeightPx;
    }

    @Override
    public int getIndexOfCurrentCamera() {
        return mCameraIndex;
    }

    @Override
    public int getNumberOfCameras() {
        return mNumCameras;
    }

    @Override
    public float getVerticalFOV() {
        return mVerticalFOV;
    }

    @Override
    public float getHorizontalFOV() {
        return mHorizontalFOV;
    }

    @Override
    public double getAspectRatio() {
        return mAspectRatio;
    }

    @Override
    public boolean isFrontCamera() {
        return !mIsCameraBackFacing;
    }

    @Override
    public boolean isBackCamera() {
        return mIsCameraBackFacing;
    }

    @Override
    public boolean isMultipleCameras() {
        if(mNumCameras > 1){return true;}
        else {return false;}
    }


    @Override
    public void useCamera(int index) {
        mCameraIndex = index;
        //TODO use this index
    }

    @Override
    public void calibrate() {
        //TODO implement how to calibrate camera

    }

    @Override
    public MatOfDouble getCameraProjectionMatrix() {
        if(mCameraMatrixDirty){
            if(mCameraMatrix == null){
                mCameraMatrix = new MatOfDouble();
                mCameraMatrix.create(3,3, CvType.CV_64FC1);
            }
            final float fovAspectRatio = mHorizontalFOV / mVerticalFOV;
            final double diagonalPx = Math.sqrt((Math.pow(mWidthPx,2.0) + Math.pow(mWidthPx / fovAspectRatio,2.0)));
            final double focalLengthPx = 0.5 * diagonalPx / Math.sqrt(
                    Math.pow(Math.tan(0.5 * mHorizontalFOV * Math.PI / 180f), 2.0) +
                            Math.pow(Math.tan(0.5 * mVerticalFOV * Math.PI / 180f), 2.0)
            );

            //TODO verify these values
            mCameraMatrix.put(0, 0, focalLengthPx);
            mCameraMatrix.put(0, 1, 0.0);
            mCameraMatrix.put(0, 2, 0.5 * mWidthPx);
            mCameraMatrix.put(1, 0, 0.0);
            mCameraMatrix.put(1, 1, focalLengthPx);
            mCameraMatrix.put(1, 2, 0.5 * mHeightPx);
            mCameraMatrix.put(2, 0, 0.0);
            mCameraMatrix.put(2, 1, 0.0);
            mCameraMatrix.put(2, 2, 1.0);
        }

        return mCameraMatrix;

    }

    public MatOfDouble getDistortion(){
        return new MatOfDouble(0.0, 0.0, 0.0, 0.0);
    }


}
