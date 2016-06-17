package ph.edu.msuiit.circuitlens.main.view;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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
import ph.edu.msuiit.circuitlens.ui.SettingsActivity;
import ph.edu.msuiit.circuitlens.ui.gl.ArRajawaliRenderer;

/* VIEW'S RESPONSIBILITY:
 *
 * Initializes all widgets and communicates ONLY with
 * the Controller.
 *
 * IMPORTANT: IT DOESN'T CARE ABOUT MODEL AND IT DOESN'T KNOW ANYTHING ABOUT THE MODEL
 */
public class CircuitLensView {
    private AppCompatActivity mActivity;
    private CircuitLensController mController;

    /* Android widgets */
    private TextView upperLeftTextView;
    private TextView bottomLeftTextView;
    private RajawaliSurfaceView mSurface;
    private ArRajawaliRenderer mRenderer;
    private CameraBridgeViewBase mOpenCvCameraView;
    private FloatingActionButton settingsButton;
    private FloatingActionButton focusButton;
    private FloatingActionButton playButton;

    public CircuitLensView(AppCompatActivity activity) {
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

        upperLeftTextView = (TextView) mActivity.findViewById(R.id.time_text_view);
        bottomLeftTextView = (TextView) mActivity.findViewById(R.id.info_text_view);

        mSurface = (RajawaliSurfaceView) mActivity.findViewById(R.id.rajawali_surface);
        mSurface.setOnTouchListener(mTouchListener);
        mSurface.setTransparent(true);

        settingsButton = (FloatingActionButton) mActivity.findViewById(R.id.settingsButton);
        focusButton = (FloatingActionButton) mActivity.findViewById(R.id.focusButton);
        playButton = (FloatingActionButton) mActivity.findViewById(R.id.playButton);

        settingsButton.setOnClickListener(onClickListener);
        focusButton.setOnClickListener(onClickListener);
        playButton.setOnClickListener(onClickListener);

        mOpenCvCameraView.setCvCameraViewListener(mController);
    }

    public void showMessage(String message) {
        Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
    }

    public void setUpperLeftText(final String text) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                upperLeftTextView.setText(text);
            }
        });
    }

    public void setBottomLeftText(final String text) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bottomLeftTextView.setText(text);
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

    public void disableCameraView() {
        mOpenCvCameraView.disableView();
    }

    public void enableCameraView() {
        mOpenCvCameraView.enableView();
    }

    public Camera getCamera() {
        Camera rajawaliCamera = null;
        if (mRenderer != null)
            rajawaliCamera = mRenderer.getCurrentCamera();
        return rajawaliCamera;
    }

    public int getViewportWidth() {
        return mRenderer.getViewportWidth();
    }

    public int getViewportHeight() {
        return mRenderer.getViewportHeight();
    }

    public void openSettingsScreen(){
        Intent intent = new Intent(mActivity, SettingsActivity.class);
        mActivity.startActivity(intent);
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

    protected View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mController.onTap(event.getX(), event.getY());
            }
            return true;
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.settingsButton:
                    mController.onSettingsButtonClick();
                    break;
                case R.id.focusButton:
                    mController.onFocusButtonClick();
                    break;
                case R.id.playButton:
                    mController.onPlayButtonClick();
                    break;
                default:
                    break;
            }
        }
    };
}