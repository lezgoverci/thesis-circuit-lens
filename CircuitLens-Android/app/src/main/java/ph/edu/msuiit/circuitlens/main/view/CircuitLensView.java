package ph.edu.msuiit.circuitlens.main.view;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.surface.RajawaliSurfaceView;

import ph.edu.msuiit.circuitlens.R;
import ph.edu.msuiit.circuitlens.cirsim.Circuit3D;
import ph.edu.msuiit.circuitlens.main.controller.CircuitLensController;
import ph.edu.msuiit.circuitlens.ui.gl.ArRajawaliRenderer;

/* VIEW'S RESPONSIBILITY:
 *
 * Initializes all widgets and communicates ONLY with
 * the Controller.
 *
 * IMPORTANT: IT DOESN'T CARE ABOUT MODEL AND IT DOESN'T KNOW ANYTHING ABOUT THE MODEL
 */
public class CircuitLensView {
    private Activity mActivity;
    private CircuitLensController mController;

    /* Android widgets */
    private TextView bottomLeftTextView;
    private TextView bottomRightTextView;
    private RajawaliSurfaceView mSurface;
    private ArRajawaliRenderer mRenderer;
    private CameraBridgeViewBase mOpenCvCameraView;

    public CircuitLensView(Activity activity) {
        mActivity = activity;
        initializeViews();
    }

    public void setupRenderer() {
        mRenderer = new ArRajawaliRenderer(mActivity, mController);
        mSurface.setSurfaceRenderer(mRenderer);
        mSurface.setZOrderMediaOverlay(true);
    }

    public void initializeViews() {
        Log.d(getClass().getSimpleName(), "initializeViews()");
        mOpenCvCameraView = (CameraBridgeViewBase) mActivity.findViewById(R.id.surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        bottomLeftTextView = (TextView) mActivity.findViewById(R.id.time_text_view);
        bottomRightTextView = (TextView) mActivity.findViewById(R.id.info_text_view);

        mSurface = (RajawaliSurfaceView) mActivity.findViewById(R.id.rajawali_surface);
        mSurface.setOnTouchListener(mTouchListener);
        mSurface.setTransparent(true);

        mOpenCvCameraView.setCvCameraViewListener(mController);
    }

    public void showMessage(String message) {
        Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
    }

    public void setBottomLeftText(final String text) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bottomLeftTextView.setText(text);
            }
        });
    }

    public void setBottomRightText(final String text) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bottomRightTextView.setText(text);
            }
        });
    }

    public void showCircuit(Circuit3D circuitCanvas) {
        mRenderer.showCircuit(circuitCanvas);
    }

    public void bind(CircuitLensController controller) {
        mController = controller;
        setupRenderer();
    }

    protected View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mController.onTap(event.getX(), event.getY());
            }
            return true;
        }
    };

    public void disableCameraView() {
        mOpenCvCameraView.disableView();
    }

    public void enableCameraView() {
        mOpenCvCameraView.enableView();
    }

    public Camera getCamera() {
        Camera rajawaliCamera = null;
        if(mRenderer != null)
            rajawaliCamera = mRenderer.getCurrentCamera();
        return rajawaliCamera;
    }

    public int getViewportWidth() {
        return mRenderer.getViewportWidth();
    }

    public int getViewportHeight() {
        return mRenderer.getViewportHeight();
    }

    // Set orientation
    //void setOrientation(double roll, double yaw, double pitch);
    //void setOrientationRoll(double roll);
    //void setOrientationYaw(double yaw);
    //void setOrientationPitch(double pitch);

    // Set translation
    //void setTranslation(double x, double y, double z);
    //void setTranslationX(double x);
    //void setTranslationY(double y);
    //void setTranslationZ(double z);

    // Set scaling
    //void setScaleTransformation(double x, double y, double z);
    //void setScaleTransformationX(double x);
    //void setScaleTransformationY(double y);
    //void setScaleTransformationZ(double z);

    // Matrices
    //void setCameraMatrix(double[] cameraMatrix);
    //void setProjectionMatrix(double[] projectionMatrix);

    //void setTrigger(boolean isTakePhoto);
}
