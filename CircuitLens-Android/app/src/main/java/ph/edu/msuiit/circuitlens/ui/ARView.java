package ph.edu.msuiit.circuitlens.ui;

import android.os.Bundle;
import android.view.SurfaceView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.MatOfDouble;
import org.rajawali3d.surface.RajawaliSurfaceView;

import ph.edu.msuiit.circuitlens.R;
import ph.edu.msuiit.circuitlens.render.CameraProjectionAdapter;
import ph.edu.msuiit.circuitlens.ui.gl.OpenGLRenderer;
import ph.edu.msuiit.circuitlens.ui.gl.RendererTransformations;

/**
 * Created by vercillius on 5/30/2016.
 */
public class ARView extends ARActivity implements CircuitLensView{

    private RendererTransformations mRenderer;
    private RajawaliSurfaceView mSurface;


    public ARView(){
        // initialize non-Android member variables
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO put in onResume?
        //mRenderer = new OpenGLRenderer(this);
        mRenderer = new RendererTransformations(this);

        mSurface = (RajawaliSurfaceView) findViewById(R.id.rajawali_surface);
        mSurface.setOnTouchListener(mTouchListener);
        mSurface.setTransparent(true);
        mSurface.setSurfaceRenderer(mRenderer);
        mSurface.setZOrderMediaOverlay(true);


        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.surfaceView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        mOpenCvCameraView.enableView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void setOrientation(double roll, double yaw, double pitch) {
        mRenderer.setOrientation(roll,yaw,pitch);

    }

    @Override
    public void setOrientationRoll(double roll) {
        mRenderer.setOrientationRoll(roll);

    }

    @Override
    public void setOrientationYaw(double yaw) {
        mRenderer.setOrientationYaw(yaw);

    }

    @Override
    public void setOrientationPitch(double pitch) {
        mRenderer.setOrientationPitch(pitch);

    }

    @Override
    public void setTranslation(double x, double y, double z) {
        mRenderer.setTranslation(x,y,z);

    }

    @Override
    public void setTranslationX(double x) {
        mRenderer.setTranslationX(x);

    }

    @Override
    public void setTranslationY(double y) {
        mRenderer.setTranslationY(y);

    }

    @Override
    public void setTranslationZ(double z) {
        mRenderer.setTranslationZ(z);

    }

    @Override
    public void setScaleTransformation(double x, double y, double z) {
        mRenderer.setScaleTransformation(x,y,z);

    }

    @Override
    public void setScaleTransformationX(double x) {
        mRenderer.setScaleTransformationX(x);

    }

    @Override
    public void setScaleTransformationY(double y) {
        mRenderer.setScaleTransformationY(y);

    }

    @Override
    public void setScaleTransformationZ(double z) {
        mRenderer.setScaleTransformationZ(z);

    }

    @Override
    public void setCameraMatrix(double[] cameraMatrix) {

        int width = (int)cameraMatrix[0];
        int height = (int)cameraMatrix[1];
        double vertical = cameraMatrix[2];
        double horizontal = cameraMatrix[3];
        double aspectRatio = cameraMatrix[4];

        mRenderer.setCameraValues(width,height,vertical,horizontal,aspectRatio);

    }

    @Override
    public void setProjectionMatrix(double[] projectionMatrix) {

    }


}
