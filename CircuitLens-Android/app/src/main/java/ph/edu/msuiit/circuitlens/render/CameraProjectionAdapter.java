package ph.edu.msuiit.circuitlens.render;

import android.hardware.Camera;
import android.os.Build;

import org.opencv.core.CvType;
import org.opencv.core.MatOfDouble;

import java.util.List;


/**
 * Created by vercillius on 5/22/2016.
 */
@SuppressWarnings("deprecation")
public class CameraProjectionAdapter {
    private double mAspectRatio;
    float mFOVY = 45f; // equivalent in 35mm photography: 28mm lens
    float mFOVX = 60f; // equivalent in 35mm photography: 28mm lens
    int mHeightPx = 480;
    int mWidthPx = 640;
    float mNear = 0.1f;
    float mFar = 10f;

    private Camera mCamera;
    private Camera.Parameters mCameraParameters;
    private Camera.Size mImageSize;
    private List<Camera.Size> mSupportedImageSizes; // The image sizes supported by the active camera.

    private int mCameraIndex;
    private int mCameraSizeIndex;
    private boolean mIsCameraBackFacing;
    private int mNumCameras;

    final float[] mProjectionGL = new float[16];
    boolean mProjectionDirtyGL = true;

    MatOfDouble mProjectionCV = null;
    boolean mProjectionDirtyCV = true;

    public CameraProjectionAdapter(){

        // default values for single camera phone
        mCameraIndex = 0;
        mCameraSizeIndex = 0;
        mNumCameras = 1;
        mIsCameraBackFacing = true;

        // check if device supports front and back camera.
        // else, use back camera
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraIndex,cameraInfo);
            mNumCameras = Camera.getNumberOfCameras();
            if(mNumCameras > 1){
                mIsCameraBackFacing = (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK);
                if(!mIsCameraBackFacing){
                    mCameraIndex++;
                    if(mCameraIndex == mNumCameras){
                        mCameraIndex = 0;
                    }
                }
            }
            mCamera = Camera.open(mCameraIndex);

        } else{
            mCamera.open();

        }
        mCameraParameters = mCamera.getParameters();
        mCamera.release();

        mSupportedImageSizes = mCameraParameters.getSupportedPreviewSizes();
        mImageSize = mSupportedImageSizes.get(mCameraSizeIndex);

        setCameraParameters();

    }

    public double getAspectRatio() {
        mAspectRatio = (double) mWidthPx / (double) mHeightPx;
        return  mAspectRatio;
    }

    public void setCameraParameters() {

        mFOVY = mCameraParameters.getVerticalViewAngle();
        mFOVX = mCameraParameters.getHorizontalViewAngle();

        mHeightPx = mImageSize.height;
        mWidthPx = mImageSize.width;

        mProjectionDirtyGL = true;
        mProjectionDirtyCV = true;
    }

    public MatOfDouble getProjectionCV(){
        if(mProjectionDirtyCV){
            if(mProjectionCV == null){
                mProjectionCV = new MatOfDouble();
                mProjectionCV.create(3,3, CvType.CV_64FC1);
            }

            final float fovAspectRatio = mFOVX / mFOVY;
            final double diagonalPx = Math.sqrt((Math.pow(mWidthPx, 2.0) +  Math.pow(mWidthPx / fovAspectRatio, 2.0)));
            final double focalLengthPx = 0.5 * diagonalPx / Math.sqrt(
                            Math.pow(Math.tan(0.5 * mFOVX * Math.PI / 180f), 2.0) +
                            Math.pow(Math.tan(0.5 * mFOVY * Math.PI / 180f), 2.0));
            mProjectionCV.put(0, 0, focalLengthPx);
            mProjectionCV.put(0, 1, 0.0);
            mProjectionCV.put(0, 2, 0.5 * mWidthPx);
            mProjectionCV.put(1, 0, 0.0);
            mProjectionCV.put(1, 1, focalLengthPx);
            mProjectionCV.put(1, 2, 0.5 * mHeightPx);
            mProjectionCV.put(2, 0, 0.0);
            mProjectionCV.put(2, 1, 0.0);
            mProjectionCV.put(2, 2, 1.0);
        }

        return mProjectionCV;
    }

}
