package ph.edu.msuiit.circuitlens.ui;

import android.os.Bundle;
import android.view.SurfaceView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.MatOfDouble;
import org.rajawali3d.surface.RajawaliSurfaceView;

import ph.edu.msuiit.circuitlens.R;
import ph.edu.msuiit.circuitlens.render.CameraProjectionAdapter;
import ph.edu.msuiit.circuitlens.ui.gl.OpenGLRenderer;

/**
 * Created by vercillius on 5/30/2016.
 */
public class ARView extends ARActivity implements CircuitLensView{

    private OpenGLRenderer mRenderer;
    private RajawaliSurfaceView mSurface;


    public ARView(){
        // initialize non-Android member variables
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRenderer = new OpenGLRenderer(this);

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
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void updateRendererCameraPose(MatOfDouble rVec, MatOfDouble tVec, int[] floats) {

    }

    @Override
    public void setRotation(double xAxisRotation, double yAxisRotation, double zAxisRotation) {

    }

    @Override
    public void setRotationX(double xAxisRotation) {

    }

    @Override
    public void setRotationY(double yAxisRotation) {

    }

    @Override
    public void setRotationZ(double zAxisRotation) {

    }

    @Override
    public void setTranslation(double x, double y, double z) {

    }

    @Override
    public void setTranslationX(double x) {

    }

    @Override
    public void setTranslationY(double y) {

    }

    @Override
    public void setTranslationZ(double z) {

    }

    @Override
    public void setScaleTransformation(double x, double y, double z) {

    }

    @Override
    public void setScaleTransformationX(double x) {

    }

    @Override
    public void setScaleTransformationY(double y) {

    }

    @Override
    public void setScaleTransformationZ(double z) {

    }

    @Override
    public void setCameraMatrix(double[] cameraMatrix) {

    }

    @Override
    public void setProjectionMatrix(double[] projectionMatrix) {

    }


}
