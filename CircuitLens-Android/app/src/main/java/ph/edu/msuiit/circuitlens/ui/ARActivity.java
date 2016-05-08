package ph.edu.msuiit.circuitlens.ui;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import ph.edu.msuiit.circuitlens.CircuitLensController;
import ph.edu.msuiit.circuitlens.RemoteNetlistGenerator;
import ph.edu.msuiit.circuitlens.R;

import static org.opencv.imgproc.Imgproc.cvtColor;

public class ARActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, CircuitLensView {
    private static final String TAG = "CircuitLens::ARActivity";
    private long startTime;
    private CircuitLensController mController;

    private CameraBridgeViewBase mOpenCvCameraView;

    // V: camera shutter
    private boolean mTakePhoto = false;
    private boolean isSetTrackingImage = false;
    private boolean isMapped;
    private boolean isDrawn;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.d(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    String str = mController.onResume();
                    Toast.makeText(ARActivity.this, str, Toast.LENGTH_SHORT).show();


                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else{
            View decorView = getWindow().getDecorView();
            // Hide the status bar in Jellybean up
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
        setContentView(R.layout.activity_ar);

        mController = new CircuitLensController(this);
        mController.onCreate();
        initializeViews();




    }

    private void initializeViews(){
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.surfaceView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);

        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        mController.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        startTime = System.currentTimeMillis();
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat frame = inputFrame.rgba();
        long currentTime = System.currentTimeMillis();
        if((currentTime - startTime) >= 3000){
            Log.d(TAG,"diff: "+String.valueOf(currentTime-startTime));
            startTime = currentTime;

            // TODO: check if image is blurry

            mController.onFocus(inputFrame);
        }

        Mat f = mController.map(frame,mTakePhoto);

        if(mTakePhoto){
            mTakePhoto = false;
        }



        return f;
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);
        mTakePhoto = true;
        return ret;
    }

    


}